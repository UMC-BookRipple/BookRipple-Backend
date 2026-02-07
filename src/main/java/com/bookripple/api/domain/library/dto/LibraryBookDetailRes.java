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
        BigDecimal progressPercent
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
                .build();
    }
}
