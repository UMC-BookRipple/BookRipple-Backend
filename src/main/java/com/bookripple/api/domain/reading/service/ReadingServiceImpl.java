package com.bookripple.api.domain.reading.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.code.ReadingErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.library.repository.LibraryItemRepository;
import com.bookripple.api.domain.reading.entity.ReadingRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.reading.converter.ReadingConverter;
import com.bookripple.api.domain.reading.dto.ReadingDto;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.entity.ReadingSession;
import com.bookripple.api.domain.reading.repository.ReadingStore;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingServiceImpl implements ReadingService {

    private final ReadingStore store;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LibraryItemRepository libraryItemRepository;

    @Override
    public ReadingDto.StartRes start(Long memberId, ReadingDto.StartReq req) {
        Long bookId = req.getBookId();

        store.findActiveSession(memberId, bookId).ifPresent(s -> {
            throw new ApiException(ReadingErrorCode.ACTIVE_SESSION_ALREADY_EXISTS);
        });

        var activeOpt = store.findActiveSession(memberId, bookId);
        if (activeOpt.isPresent()) {
            return ReadingConverter.toStartRes(activeOpt.get());
        }

        var pausedOpt = store.findPausedSession(memberId, bookId);
        if (pausedOpt.isPresent()) {
            ReadingSession paused = pausedOpt.get();
            paused.resume();
            return ReadingConverter.toStartRes(paused);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

        ReadingSession session = ReadingConverter.toSession(member, book);
        store.saveSession(session);

        store.getOrCreateProgress(member, book);
        markAsReadingIfNotCompleted(member, book);

        return ReadingConverter.toStartRes(session);
    }

    @Override
    public ReadingDto.PauseRes pause(Long memberId, Long sessionId) {
        ReadingSession session = store.findSessionByIdAndMember(sessionId, memberId)
                .orElseThrow(() -> new ApiException(ReadingErrorCode.NO_READING_SESSION));

        session.pause();

        return ReadingConverter.toPauseRes(session);
    }

    @Override
    public ReadingDto.EndRes end(Long memberId, ReadingDto.EndReq req) {
        ReadingSession session = store.findSessionByIdAndMember(req.getSessionId(), memberId)
                .orElseThrow(() -> new ApiException(ReadingErrorCode.NO_READING_SESSION));

        int sessionSeconds = session.end();

        // startPage 입력이 0일 시 1로 고정
        int startPage = req.getPagesReadStart() <= 0 ? 1 : req.getPagesReadStart();
        int endPage = req.getPagesReadEnd();

        int totalPages = session.getBook().getTotalPage();

        // endPage 유효성 검증
        if (endPage <= 0 || endPage > totalPages) {
            throw new ApiException(ReadingErrorCode.INVALID_END_PAGE);
        }

        // startPage <= endPage
        if (startPage > endPage) {
            throw new ApiException(ReadingErrorCode.INVALID_PAGE_RANGE);
        }

        ReadingRecord record = ReadingConverter.toRecord(session, sessionSeconds, startPage, endPage);
        store.saveRecord(record);

        ReadingProgress progress = store.getOrCreateProgress(session.getMember(), session.getBook());
        progress.addReadingTime(sessionSeconds);

        progress.applyRecord(record, totalPages);

        // 일별 독서 시간 저장
        store.saveDailyReadingTime(session.getMember(), LocalDate.now(), sessionSeconds);

        upsertLibraryStatus(session.getMember(), session.getBook(),
                progress.isCompleted() ? LibraryStatus.COMPLETED : LibraryStatus.READING);

        return ReadingConverter.toEndRes(record, sessionSeconds, progress);
    }


    @Override
    public ReadingDto.CompleteRes complete(Long memberId, ReadingDto.CompleteReq req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

        ReadingProgress progress = store.getOrCreateProgress(member, book);
        progress.markCompleted();

        upsertLibraryStatus(member, book, LibraryStatus.COMPLETED);

        return ReadingConverter.toCompleteRes(book.getId(), progress);
    }

    private void markAsReadingIfNotCompleted(Member member, Book book) {
        LibraryItem item = libraryItemRepository.findByMemberIdAndBook_Id(member.getId(), book.getId())
                .orElseGet(() -> LibraryItem.builder()
                        .member(member)
                        .book(book)
                        .status(LibraryStatus.READING)
                        .build()
                );

        // 이미 완독 상태면 유지
        if (item.getStatus() != LibraryStatus.COMPLETED) {
            item.setStatus(LibraryStatus.READING);
        }

        libraryItemRepository.save(item);
    }

    private void upsertLibraryStatus(Member member, Book book, LibraryStatus targetStatus) {
        LibraryItem item = libraryItemRepository.findByMemberIdAndBook_Id(member.getId(), book.getId())
                .orElseGet(() -> LibraryItem.builder()
                        .member(member)
                        .book(book)
                        .status(targetStatus)
                        .build()
                );

        // 이미 존재하는 경우에도 상태 갱신
        item.setStatus(targetStatus);

        libraryItemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadingDto.WeeklyReadingGraphRes getWeeklyReadingGraph(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 지난 7일 데이터 조회 (오늘 기준)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 7일 전부터 오늘까지

        var dailyReadingTimes = store.findWeeklyReadingTime(memberId, startDate, endDate);

        // Map으로 변환하여 조회 성능 향상
        Map<LocalDate, Integer> readingTimeMap = new HashMap<>();
        int totalReadingTime = 0;
        for (var daily : dailyReadingTimes) {
            readingTimeMap.put(daily.getReadingDate(), daily.getTotalReadingTime());
            totalReadingTime += daily.getTotalReadingTime();
        }

        // 7일간의 데이터 생성 (없는 날짜는 0분)
        List<ReadingDto.DailyReadingData> dailyList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            int readingSeconds = readingTimeMap.getOrDefault(currentDate, 0);
            int readingMinutes = readingSeconds / 60; // 초를 분으로 변환

            String dayOfWeek = currentDate.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.KOREAN);

            dailyList.add(ReadingDto.DailyReadingData.builder()
                    .date(currentDate)
                    .dayOfWeek(dayOfWeek)
                    .readingTimeMinutes(readingMinutes)
                    .build()
            );
        }

        return ReadingDto.WeeklyReadingGraphRes.builder()
                .dailyReadingList(dailyList)
                .totalReadingTime(totalReadingTime / 60) // 초를 분으로 변환
                .build();
    }

    /**
     * ReadingStore에서 주별 독서 시간 조회
     */
    private List<com.bookripple.api.domain.reading.entity.DailyReadingTime> 
            findWeeklyReadingTime(Long memberId, LocalDate startDate, LocalDate endDate) {
        return store.findWeeklyReadingTime(memberId, startDate, endDate);
    }
}
