package com.bookripple.api.domain.blindsalepost.repository;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    // 1. 특정 게시글의 전체 구매 요청 리스트 조회 (명단 확인용)
    List<PurchaseRequest> findAllByBlindSalePostId(Long blindSalePostId);

    // 2. 특정 게시글의 판매 요청 인원수 카운트
    long countByBlindSalePostId(Long blindSalePostId);

    // 3. 중복 요청 방지 확인 (이미 요청한 유저인지 체크)
    boolean existsByBlindSalePostAndMember(BlindSalePost blindSalePost, Member member);

    // 4. 특정 유저가 보낸 요청 하나 조회
    Optional<PurchaseRequest> findByBlindSalePostIdAndMemberId(Long blindSalePostId, Long memberId);
}