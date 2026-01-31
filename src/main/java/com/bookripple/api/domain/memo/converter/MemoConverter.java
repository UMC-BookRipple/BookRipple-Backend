package com.bookripple.api.domain.memo.converter;

import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;
import com.bookripple.api.domain.memo.entity.Memo;

import java.util.List;

public class MemoConverter {

    public static Item toItem(Memo memo, Long viewerMemberId) {
        boolean isMine = memo.getMember().getId().equals(viewerMemberId);

        String writerName = isMine
                ? memo.getMember().getName()
                : anonymousName(memo.getMember().getId());

        return new Item(
                memo.getId(),
                writerName,
                memo.getMemoTitle(),
                memo.getContext(),
                memo.getPage()
        );
    }

    public static MemoList toMemoList(List<Item> items, Long nextCursor, boolean hasNext) {
        return new MemoList(items, nextCursor, hasNext);
    }

    private static String anonymousName(Long memberId) {
        int code = Math.abs(memberId.hashCode()) % 10000;
        return String.format("익명의 사용자 %04d", code);
    }
}
