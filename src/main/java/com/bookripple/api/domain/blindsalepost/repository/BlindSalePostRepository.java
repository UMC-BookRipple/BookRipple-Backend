package com.bookripple.api.domain.blindsalepost.repository;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindSalePostRepository extends JpaRepository<BlindSalePost, Long> {

    // [최종형] 특정 판매자(Member)의 + 특정 상태(Status) 게시글을 + 최신순으로 + 커서 기반 조회

    // 1. 첫 페이지 조회용 (커서 없음)
    List<BlindSalePost> findAllByMemberIdAndPostStatusOrderByIdDesc(
            Long memberId, PostStatus status, Pageable pageable);

    // 2. 다음 페이지 조회용 (커서 있음: id < cursor)
    List<BlindSalePost> findAllByMemberIdAndPostStatusAndIdLessThanOrderByIdDesc(
            Long memberId, PostStatus status, Long id, Pageable pageable);

    // 판매 중인 전체 글 조회 (최신순)
    List<BlindSalePost> findAllByPostStatusOrderByIdDesc(PostStatus status, Pageable pageable);
    List<BlindSalePost> findAllByPostStatusAndIdLessThanOrderByIdDesc(PostStatus status, Long id, Pageable pageable);
}
