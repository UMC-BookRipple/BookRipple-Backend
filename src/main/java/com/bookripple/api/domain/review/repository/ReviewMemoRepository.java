package com.bookripple.api.domain.review.repository;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.review.entity.Review;
import com.bookripple.api.domain.review.entity.ReviewMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMemoRepository extends JpaRepository<ReviewMemo, Long> {

  boolean existsByReviewAndMember(Review review, Member member);
}
