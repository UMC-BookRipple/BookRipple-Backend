package com.bookripple.api.domain.blindsalepost.repository;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindSalePostRepository extends JpaRepository<BlindSalePost, Long> {
    // 1. 특정 판매자가 올린 글 목록 조회 (나의 판매 목록)
    List<BlindSalePost> findAllByMemberId(Long memberId);

    // 2. 판매 상태별 조회 (판매중/거래완료 탭 구분용)
     List<BlindSalePost> findAllByPostStatus(PostStatus status);
}
