package com.bookripple.api.domain.memo.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.MemoErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.memo.converter.MemoConverter;
import com.bookripple.api.domain.memo.dto.MemoResDto;
import com.bookripple.api.domain.memo.dto.MemoResDto.Item;
import com.bookripple.api.domain.memo.dto.MemoResDto.MemoList;
import com.bookripple.api.domain.memo.entity.Memo;
import com.bookripple.api.domain.memo.repository.MemoRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoQueryServiceImpl implements MemoQueryService {

    private final MemoRepository memoRepository;
    private final BookRepository bookRepository;

    @Override
    public MemoList getBookMemos(Long viewerMemberId, Long bookId, Long lastId, int size) {

        if (!bookRepository.existsById(bookId)) {
            throw new ApiException(BookErrorCode.NO_BOOK);
        }

        Long cursor = (lastId == null) ? Long.MAX_VALUE : lastId;
        Pageable pageable = PageRequest.of(0, size);

        Slice<Memo> memoSlice =
                memoRepository.findByBookIdAndIdLessThanOrderByIdDesc(bookId, cursor, pageable);

        List<Item> items = memoSlice.getContent().stream()
                .map(memo -> MemoConverter.toItem(memo, viewerMemberId))
                .toList();

        Long nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).memoId();

        return MemoConverter.toMemoList(items, nextCursor, memoSlice.hasNext());
    }

    @Override
    public Item getMemoDetail(Long viewerMemberId, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new ApiException(MemoErrorCode.NO_MEMO));
        return MemoConverter.toItem(memo, viewerMemberId);
    }

    @Override
    public MemoResDto.MyMemoList getMyMemos(Long memberId, Long lastMemoId, int size) {

        Long cursor = (lastMemoId == null) ? Long.MAX_VALUE : lastMemoId;
        Pageable pageable = PageRequest.of(0, size);

        Slice<Memo> memoSlice =
                memoRepository.findByMemberIdAndIdLessThanOrderByIdDesc(memberId, cursor, pageable);

        List<Item> items = memoSlice.getContent().stream()
                .map(memo -> MemoConverter.toItem(memo, memberId))
                .toList();

        Long nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).memoId();

        return new MemoResDto.MyMemoList(items, nextCursor, memoSlice.hasNext());
    }

    @Override
    public MemoResDto.MemoList getMyBookMemos(Long memberId, Long bookId, Long lastId, int size) {

        if (!bookRepository.existsById(bookId)) {
            throw new ApiException(BookErrorCode.NO_BOOK);
        }

        Long cursor = (lastId == null) ? Long.MAX_VALUE : lastId;
        Pageable pageable = PageRequest.of(0, size);

        Slice<Memo> memoSlice =
                memoRepository.findByBookIdAndMemberIdAndIdLessThanOrderByIdDesc(
                        bookId, memberId, cursor, pageable
                );

        List<Item> items = memoSlice.getContent().stream()
                .map(memo -> MemoConverter.toItem(memo, memberId)) // viewer = 나
                .toList();

        Long nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).memoId();

        return MemoConverter.toMemoList(items, nextCursor, memoSlice.hasNext());
    }


}
