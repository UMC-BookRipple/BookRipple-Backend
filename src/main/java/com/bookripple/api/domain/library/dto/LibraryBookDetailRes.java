package com.bookripple.api.domain.library.dto;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record LibraryBookDetailRes(
        Long bookId,
        String title,
        String coverUrl,
        List<String> authors,
        String publisher,
        Integer totalPages,
        LibraryStatus status,
        BigDecimal progressPercent,
        
        // 추가: 도서별 독서 정보
        Integer readingTimeMinutes,      // 도서별 총 독서 시간 (분)
        Double readingSpeed,             // 독서 진행 속도 (페이지/일)
        Integer estimatedDaysToCompletion // 완독까지 남은 일수 (READING 상태일 때만)
) {
    public static LibraryBookDetailRes of(LibraryItem item, ReadingProgress progress) {
        Book book = item.getBook();

        return LibraryBookDetailRes.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .coverUrl(book.getBookCover())
                .authors(List.of(book.getAuthor()))
                .publisher(book.getPublisher())
                .totalPages(book.getTotalPage())
                .status(item.getStatus())
                .progressPercent(progress.getProgress())
                .readingTimeMinutes(progress.getReadingTime() / 60)  // 초를 분으로 변환
                .readingSpeed(0.0)  // 추후 계산
                .estimatedDaysToCompletion(null)  // 추후 계산
                .build();
    }
    
    /**
     * 읽는 속도와 완독 예상일을 포함한 생성자
     */
    public static LibraryBookDetailRes ofWithReadingStats(
            LibraryItem item, 
            ReadingProgress progress,
            Double readingSpeed,
            Integer estimatedDaysToCompletion) {
        Book book = item.getBook();

        return LibraryBookDetailRes.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .coverUrl(book.getBookCover())
                .authors(List.of(book.getAuthor()))
                .publisher(book.getPublisher())
                .totalPages(book.getTotalPage())
                .status(item.getStatus())
                .progressPercent(progress.getProgress())
                .readingTimeMinutes(progress.getReadingTime() / 60)  // 초를 분으로 변환
                .readingSpeed(readingSpeed)
                .estimatedDaysToCompletion(estimatedDaysToCompletion)
                .build();
    }
}
