package com.bookripple.api.domain.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.bookripple.api.common.code.LibraryErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.library.dto.LibraryBookDetailRes;
import com.bookripple.api.domain.library.dto.LibraryBookSummaryListRes;
import com.bookripple.api.domain.library.dto.LibraryBookSummaryRes;
import com.bookripple.api.domain.library.dto.LibraryDto;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.entity.ReadingRecord;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
import com.bookripple.api.domain.reading.repository.ReadingRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.library.dto.LibraryItemListRes;
import com.bookripple.api.domain.library.dto.LibraryItemRes;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.repository.LibraryItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryQueryServiceImpl implements LibraryQueryService {

    private final LibraryItemRepository libraryItemRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final ReadingRecordRepository readingRecordRepository;

    @Override
    public LibraryItemListRes getMyLibrary(Long memberId, LibraryStatus status, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        //like 기준 조회
        if (status == LibraryStatus.LIKED) {

            List<ReadingProgress> fetched = (lastId == null)
                    ? readingProgressRepository.findByMemberIdAndIsLikedTrueOrderByIdDesc(memberId, pageable)
                    : readingProgressRepository.findByMemberIdAndIsLikedTrueAndIdLessThanOrderByIdDesc(memberId, lastId, pageable);

            boolean hasNext = fetched.size() > size;
            if (hasNext) {
                fetched = fetched.subList(0, size);
            }

            List<LibraryItemRes> items = fetched.stream()
                    .map(LibraryItemRes::from)
                    .toList();

            Long nextLastId = fetched.isEmpty() ? null : fetched.get(fetched.size() - 1).getId(); // rp.id

            return LibraryItemListRes.of(items, hasNext, nextLastId);
        }

        // reading, completed 기준 조회하는 경우
        List<LibraryItem> fetched = (lastId == null)
                ? libraryItemRepository.findByMemberIdAndStatusOrderByIdDesc(memberId, status, pageable)
                : libraryItemRepository.findByMemberIdAndStatusAndIdLessThanOrderByIdDesc(
                memberId, status, lastId, pageable
        );

        boolean hasNext = fetched.size() > size;
        if (hasNext) {
            fetched = fetched.subList(0, size);
        }

        List<LibraryItemRes> items = fetched.stream()
                .map(LibraryItemRes::from)
                .toList();

        Long nextLastId = fetched.isEmpty() ? null : fetched.get(fetched.size() - 1).getId();

        return LibraryItemListRes.of(items, hasNext, nextLastId);
    }

    @Override
    public LibraryDto.DeleteRes deleteBooks(Long memberId, LibraryStatus status, LibraryDto.DeleteReq request) {

        long deleted = libraryItemRepository.deleteByMemberIdAndStatusAndBook_IdIn(
                memberId,
                status,
                request.getBookIds()
        );

        return LibraryDto.DeleteRes.of(deleted);
    }

    @Override
    public LibraryBookDetailRes getMyLibraryBookDetail(Long memberId, Long bookId) {
        LibraryItem item = libraryItemRepository.findByMemberIdAndBook_Id(memberId, bookId)
                .orElseThrow(() -> new ApiException(LibraryErrorCode.NO_LIBRARY_BOOK));

        ReadingProgress progress = readingProgressRepository.findByMemberIdAndBookId(memberId, bookId);
        
        // 읽는 속도와 완독 예상일 계산
        Double readingSpeed = null;
        Integer estimatedDaysToCompletion = null;
        
        // READING 상태일 때만 계산
        if (item.getStatus() == LibraryStatus.READING) {
            // 첫 독서 기록 조회
            var firstRecordOpt = readingRecordRepository.findFirstByMemberIdAndBookIdOrderByCreatedAtAsc(memberId, bookId);
            // 최근 독서 기록 조회
            var latestRecordOpt = readingRecordRepository.findLatestByMemberIdAndBookId(memberId, bookId);
            
            if (firstRecordOpt.isPresent() && latestRecordOpt.isPresent()) {
                ReadingRecord firstRecord = firstRecordOpt.get();
                ReadingRecord latestRecord = latestRecordOpt.get();
                
                // 독서 시작 날짜
                LocalDate readingStartDate = firstRecord.getCreatedAt().toLocalDate();
                LocalDate today = LocalDate.now();
                
                // 독서 시작부터 오늘까지의 일 수
                long daysSinceStart = ChronoUnit.DAYS.between(readingStartDate, today);
                
                // 최근 기록까지 읽은 총 페이지 수 (현재 progress의 진행률로 계산)
                int totalPages = item.getBook().getTotalPage();
                int pagesRead = (int) (totalPages * progress.getProgress().doubleValue() / 100.0);
                
                // 독서 진행 속도 (페이지/일)
                if (daysSinceStart > 0) {
                    readingSpeed = Math.round((double) pagesRead / daysSinceStart * 100.0) / 100.0;
                    
                    // 남은 페이지
                    int remainingPages = totalPages - pagesRead;
                    
                    // 완독까지 예상 일수
                    if (readingSpeed > 0) {
                        estimatedDaysToCompletion = (int) Math.ceil(remainingPages / readingSpeed);
                    }
                } else if (pagesRead > 0) {
                    // 같은 날에 읽은 경우
                    readingSpeed = (double) pagesRead;
                    
                    int remainingPages = totalPages - pagesRead;
                    if (readingSpeed > 0 && remainingPages > 0) {
                        estimatedDaysToCompletion = (int) Math.ceil(remainingPages / readingSpeed);
                    } else {
                        estimatedDaysToCompletion = 0;
                    }
                }
            }
        }
        
        return LibraryBookDetailRes.ofWithReadingStats(item, progress, readingSpeed, estimatedDaysToCompletion);
    }

    @Override
    public LibraryBookSummaryListRes getMyLibraryBooksSummary(Long memberId) {
        // 사용자의 READING 상태의 모든 책 조회
        List<LibraryItem> items = libraryItemRepository.findByMemberIdAndStatusOrderByIdDesc(
                memberId, 
                LibraryStatus.READING, 
                Pageable.unpaged()
        );

        List<LibraryBookSummaryRes> summaries = new ArrayList<>();

        for (LibraryItem item : items) {
            Long bookId = item.getBook().getId();
            
            // 읽기 진행 상황 조회
            ReadingProgress progress = readingProgressRepository.findByMemberIdAndBookId(memberId, bookId);
            
            if (progress == null) {
                // 진행 상황이 없으면 기본값으로 생성
                summaries.add(LibraryBookSummaryRes.of(item, progress));
                continue;
            }

            // READING 상태일 때만 완독 예상일 계산
            Integer estimatedDaysToCompletion = null;
            
            if (item.getStatus() == LibraryStatus.READING) {
                // 첫 독서 기록 조회
                var firstRecordOpt = readingRecordRepository.findFirstByMemberIdAndBookIdOrderByCreatedAtAsc(memberId, bookId);
                // 최근 독서 기록 조회
                var latestRecordOpt = readingRecordRepository.findLatestByMemberIdAndBookId(memberId, bookId);
                
                if (firstRecordOpt.isPresent() && latestRecordOpt.isPresent()) {
                    ReadingRecord firstRecord = firstRecordOpt.get();
                    
                    // 독서 시작 날짜
                    LocalDate readingStartDate = firstRecord.getCreatedAt().toLocalDate();
                    LocalDate today = LocalDate.now();
                    
                    // 독서 시작부터 오늘까지의 일 수
                    long daysSinceStart = ChronoUnit.DAYS.between(readingStartDate, today);
                    
                    // 최근 기록까지 읽은 총 페이지 수
                    int totalPages = item.getBook().getTotalPage();
                    int pagesRead = (int) (totalPages * progress.getProgress().doubleValue() / 100.0);
                    
                    // 독서 진행 속도 (페이지/일)
                    Double readingSpeed = null;
                    if (daysSinceStart > 0) {
                        readingSpeed = (double) pagesRead / daysSinceStart;
                        
                        // 남은 페이지
                        int remainingPages = totalPages - pagesRead;
                        
                        // 완독까지 예상 일수
                        if (readingSpeed > 0) {
                            estimatedDaysToCompletion = (int) Math.ceil(remainingPages / readingSpeed);
                        }
                    } else if (pagesRead > 0) {
                        // 같은 날에 읽은 경우
                        readingSpeed = (double) pagesRead;
                        
                        int remainingPages = totalPages - pagesRead;
                        if (readingSpeed > 0 && remainingPages > 0) {
                            estimatedDaysToCompletion = (int) Math.ceil(remainingPages / readingSpeed);
                        } else {
                            estimatedDaysToCompletion = 0;
                        }
                    }
                }
            }
            
            summaries.add(LibraryBookSummaryRes.ofWithReadingStats(item, progress, estimatedDaysToCompletion));
        }

        return LibraryBookSummaryListRes.of(summaries);
    }
}
