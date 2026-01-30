package com.bookripple.api.domain.library.dto;

import java.util.List;

import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
public record LibraryItemRes(
        Long libraryItemId,
        Long bookId,
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
                .title(book.getTitle())
                .coverUrl(book.getBookCover())
                .authors(List.of(book.getAuthor()))
                .status(item.getStatus())
                .build();
    }
}
