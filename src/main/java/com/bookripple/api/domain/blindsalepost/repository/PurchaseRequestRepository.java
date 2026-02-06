package com.bookripple.api.domain.blindsalepost.repository;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import com.bookripple.api.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    // 특정 게시글의 전체 구매 요청 리스트 조회 (명단 확인용)
    List<PurchaseRequest> findAllByBlindSalePostId(Long blindSalePostId);

    // 특정 게시글의 판매 요청 인원수 카운트
    long countByBlindSalePostId(Long blindSalePostId);

    // 중복 요청 방지 확인 (이미 요청한 유저인지 체크)
    boolean existsByBlindSalePostAndMember(BlindSalePost blindSalePost, Member member);

    // 특정 유저가 보낸 요청 하나 조회
    Optional<PurchaseRequest> findByBlindSalePostIdAndMemberId(Long blindSalePostId, Long memberId);

    // 내가 보낸 요청 목록 조회
    List<PurchaseRequest> findAllByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);
    List<PurchaseRequest> findAllByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long id, Pageable pageable);

    // 특정 게시글에서 현재 '수락(ACCEPTED)' 상태인 요청을 찾습니다.
    Optional<PurchaseRequest> findByBlindSalePostIdAndStatus(Long blindSalePostId, PurchaseStatus status);

    @Modifying
    @Query("UPDATE PurchaseRequest p SET p.status = 'REJECTED' " +
            "WHERE p.blindSalePost.id = :postId AND p.id != :requestId AND p.status = 'WAITING'")
    void rejectOthers(@Param("postId") Long postId, @Param("requestId") Long requestId);
}