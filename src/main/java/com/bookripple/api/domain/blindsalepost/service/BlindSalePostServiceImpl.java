package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.domain.blindsalepost.converter.BlindSalePostConverter;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.BookCondition;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.blindsalepost.repository.BlindSalePostRepository;
import com.bookripple.api.domain.blindsalepost.repository.PurchaseRequestRepository;
import com.bookripple.api.domain.book.entity.Book;
import com.bookripple.api.domain.book.repository.BookRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindSalePostServiceImpl implements BlindSalePostService {

    private final BlindSalePostRepository blindSalePostRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;

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

    @Override
    public BlindSalePostResDto.Detail getPostDetail(Long blindPostId) {
        // 1. 게시글 존재 여부 확인
        BlindSalePost post = blindSalePostRepository.findById(blindPostId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        // 2. 해당 게시글에 들어온 모든 구매 요청 리스트 조회
        List<PurchaseRequest> requests = purchaseRequestRepository.findAllByBlindSalePostId(blindPostId);

        // 3. 컨버터를 통해 게시글 정보와 요청자 명단을 합쳐서 DTO로 변환
        return BlindSalePostConverter.toDetail(post, requests);
    }

    @Override
    public BlindSalePostResDto.SliceResponse getMyPostList(Long memberId, PostStatus status, Long cursor, int size) {
        // 1. 다음 페이지 확인을 위해 size + 1개를 요청합니다.
        Pageable pageable = PageRequest.of(0, size + 1);

        // 2. 수정된 Repository 메서드 호출: 내 ID와 상태값으로 필터링합니다.
        List<BlindSalePost> posts = (cursor == null)
                ? blindSalePostRepository.findAllByMemberIdAndPostStatusOrderByIdDesc(memberId, status, pageable)
                : blindSalePostRepository.findAllByMemberIdAndPostStatusAndIdLessThanOrderByIdDesc(memberId, status, cursor, pageable);

        // 3. 무한 스크롤을 위한 다음 페이지 여부 계산
        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts.remove(size);
        }

        // 4. 마지막 아이템의 ID를 다음 커서로 지정
        Long nextCursor = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();

        // 5. [Converter]를 사용하여 DTO 리스트로 변환
        List<BlindSalePostResDto.ListElement> content = posts.stream()
                .map(BlindSalePostConverter::toListElement)
                .collect(Collectors.toList());

        return BlindSalePostConverter.toSliceResponse(content, nextCursor, hasNext);
    }

    @Override
    @Transactional
    public void updatePost(Long memberId, Long blindBookId, BlindSalePostReqDto.Update request) {
        // 1. 게시글 존재 여부 확인
        BlindSalePost post = blindSalePostRepository.findById(blindBookId)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        // 2. 권한 확인: 본인의 글만 수정 가능
        if (!post.getMember().getId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 3. 엔티티의 update 메서드 호출 (Dirty Checking으로 자동 DB 반영)
        post.update(
                request.title(),
                request.subtitle(),
                request.description(),
                request.price(),
                BookCondition.valueOf(request.bookCondition())
        );
    }
}