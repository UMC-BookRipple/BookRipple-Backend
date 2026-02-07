package com.bookripple.api.domain.reading.converter;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.reading.dto.ReadingDto;
import com.bookripple.api.domain.reading.entity.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadingConverter {

    public static ReadingSession toSession(Member member, Book book) {
        return ReadingSession.builder()
                .member(member)
                .book(book)
                .build();
    }

    public static ReadingRecord toRecord(ReadingSession session, int sessionSeconds, int startPage, int endPage) {
        return ReadingRecord.builder()
                .member(session.getMember())
                .book(session.getBook())
                .startPage(startPage)
                .endPage(endPage)
                .readingTime(sessionSeconds)
                .build();
    }


    /* ===== Response DTO 생성 ===== */

    public static ReadingDto.StartRes toStartRes(ReadingSession session) {
        return ReadingDto.StartRes.builder()
                .sessionId(session.getId())
                .build();
    }

    public static ReadingDto.PauseRes toPauseRes(ReadingSession session) {
        return ReadingDto.PauseRes.builder()
                .sessionId(session.getId())
                .accumulatedTime(session.getAccumulatedTime())
                .status(session.getStatus().name())
                .build();
    }

    public static ReadingDto.EndRes toEndRes(ReadingRecord record, int sessionSeconds, ReadingProgress progress) {
        return ReadingDto.EndRes.builder()
                .recordId(record.getId())
                .readingTime(sessionSeconds)
                .totalReadingTime(progress.getReadingTime())
                .progress(progress.getProgress())
                .isCompleted(progress.isCompleted())
                .build();
    }

    public static ReadingDto.CompleteRes toCompleteRes(Long bookId, ReadingProgress progress) {
        return ReadingDto.CompleteRes.builder()
                .bookId(bookId)
                .progress(progress.getProgress())
                .isCompleted(progress.isCompleted())
                .build();
    }
}
