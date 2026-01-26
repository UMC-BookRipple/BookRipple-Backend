package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.domain.blindsalepost.converter.BlindSalePostConverter;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.repository.BlindSalePostRepository;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindSalePostServiceImpl implements BlindSalePostService {

    private final BlindSalePostRepository blindSalePostRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BlindSalePostResDto.Create createPost(Long memberId, BlindSalePostReqDto.Create request) {
        // 1. 등록할 회원과 실제 도서 정보를 조회합니다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        Book book = bookRepository.findById(request.actualBookId())
                .orElseThrow(() -> new RuntimeException("도서 정보를 찾을 수 없습니다."));

        // 2. [Converter]를 사용하여 DTO를 엔티티로 변환합니다.
        BlindSalePost post = BlindSalePostConverter.toBlindSalePost(request, member, book);

        // 3. 변환된 게시글 엔티티를 DB에 저장합니다.
        BlindSalePost savedPost = blindSalePostRepository.save(post);

        // 4. [Converter]를 사용하여 저장된 엔티티를 응답 DTO로 변환하여 반환합니다.
        return BlindSalePostConverter.toCreateResponse(savedPost);
    }
}