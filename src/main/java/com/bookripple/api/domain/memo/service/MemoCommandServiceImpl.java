package com.bookripple.api.domain.memo.service;

import com.bookripple.api.common.code.BookErrorCode;
import com.bookripple.api.common.code.MemoErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Create;
import com.bookripple.api.domain.memo.dto.MemoReqDto.Update;
import com.bookripple.api.domain.memo.entity.Memo;
import com.bookripple.api.domain.memo.repository.MemoRepository;
import com.bookripple.api.global.converter.GlobalConverter;
import com.bookripple.api.global.dto.GlobalDto.IdRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoCommandServiceImpl implements MemoCommandService {

    private final MemoRepository memoRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public IdRes createMemo(Long memberId, Long bookId, Create req) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ApiException(BookErrorCode.NO_BOOK));

        Member member = memberRepository.getReferenceById(memberId);

        Memo memo = Memo.builder()
                .context(req.contentReq().content())
                .memoTitle(req.memoTitle())
                .page(req.page())
                .book(book)
                .member(member)
                .build();

        memoRepository.save(memo);

        return GlobalConverter.toIdRes(memo.getId());
    }

    @Override
    @Transactional
    public IdRes updateMemo(Long memberId, Long memoId, Update req) {

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new ApiException(MemoErrorCode.NO_MEMO));

        if (!memo.getMember().getId().equals(memberId)) {
            throw new ApiException(MemoErrorCode.FORBIDDEN);
        }

        // PATCH: null이면 유지
        String newContext = (req.contentReq() == null) ? null : req.contentReq().content();
        memo.update(newContext, req.memoTitle(), req.page());

        return GlobalConverter.toIdRes(memoId);
    }

    @Override
    @Transactional
    public IdRes deleteMemo(Long memberId, Long memoId) {

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new ApiException(MemoErrorCode.NO_MEMO));

        if (!memo.getMember().getId().equals(memberId)) {
            throw new ApiException(MemoErrorCode.FORBIDDEN);
        }

        memoRepository.delete(memo);
        return GlobalConverter.toIdRes(memoId);
    }
}
