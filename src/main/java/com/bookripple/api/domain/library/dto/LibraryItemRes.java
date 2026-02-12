package com.bookripple.api.domain.library.dto;

import java.util.List;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;

import com.bookripple.api.domain.reading.entity.ReadingProgress;
import lombok.Builder;

@Builder
public record LibraryItemRes(
        Long libraryItemId,
        Long bookId,
        Long aladinItemId,
        String title,
        String coverUrl,
        List<String> authors,
        LibraryStatus status
) {

    public static LibraryItemRes from(LibraryItem item) {
        Book book = item.getBook();
        return LibraryItemRes.builder()
                .libraryItemId(item.getId())
                .bookId(book.getId())
                .aladinItemId(book.getAladinBookId())
                .title(book.getTitle())
                .coverUrl(book.getBookCover())
                .authors(List.of(book.getAuthor()))
                .status(item.getStatus())
                .build();
    }

    public static LibraryItemRes from(ReadingProgress rp) {
        Book book = rp.getBook();
        return LibraryItemRes.builder()
                .libraryItemId(rp.getId())
                .bookId(book.getId())
                .aladinItemId(book.getAladinBookId())
                .title(book.getTitle())
                .coverUrl(book.getBookCover())
                .authors(List.of(book.getAuthor()))
                .status(LibraryStatus.LIKED)
                .build();
    }
}
