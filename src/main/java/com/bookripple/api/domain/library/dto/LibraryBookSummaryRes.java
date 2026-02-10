package com.bookripple.api.domain.library.dto;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 마이페이지에서 여러 책의 요약 정보를 표시하기 위한 DTO
 */
@Builder
public record LibraryBookSummaryRes(
        Long bookId,
        String coverUrl,
        String title,
        List<String> authors,
        LibraryStatus status,
        BigDecimal progressPercent,
        Integer readingTimeMinutes,      // 도서별 총 독서 시간 (분)
        Integer estimatedDaysToCompletion // 완독까지 남은 일수 (READING 상태일 때만)
) {
    /**
     * 기본 생성자 (읽는 속도와 완독 예상일 없이)
     */
    public static LibraryBookSummaryRes of(LibraryItem item, ReadingProgress progress) {
        Book book = item.getBook();

        return LibraryBookSummaryRes.builder()
                .bookId(book.getId())
                .coverUrl(book.getBookCover())
                .title(book.getTitle())
                .authors(List.of(book.getAuthor()))
                .status(item.getStatus())
                .progressPercent(progress.getProgress())
                .readingTimeMinutes(progress.getReadingTime() / 60)  // 초를 분으로 변환
                .estimatedDaysToCompletion(null)
                .build();
    }

    /**
     * 읽는 속도와 완독 예상일을 포함한 생성자
     */
    public static LibraryBookSummaryRes ofWithReadingStats(
            LibraryItem item,
            ReadingProgress progress,
            Integer estimatedDaysToCompletion) {
        Book book = item.getBook();

        return LibraryBookSummaryRes.builder()
                .bookId(book.getId())
                .coverUrl(book.getBookCover())
                .title(book.getTitle())
                .authors(List.of(book.getAuthor()))
                .status(item.getStatus())
                .progressPercent(progress.getProgress())
                .readingTimeMinutes(progress.getReadingTime() / 60)  // 초를 분으로 변환
                .estimatedDaysToCompletion(estimatedDaysToCompletion)
                .build();
    }
}
