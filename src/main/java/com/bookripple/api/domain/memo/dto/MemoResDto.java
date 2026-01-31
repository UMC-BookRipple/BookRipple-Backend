package com.bookripple.api.domain.memo.dto;

import java.util.List;

public class MemoResDto {

    public record Item(
            Long memoId,
            String writerName,
            String memoTitle,
            String context,
            String page
    ) {}

    public record MemoList(
            List<Item> items,
            Long lastId,
            boolean hasNext
    ) {}
}
