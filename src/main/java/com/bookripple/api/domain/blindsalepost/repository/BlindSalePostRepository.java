package com.bookripple.api.domain.blindsalepost.repository;

import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindSalePostRepository extends JpaRepository<BlindSalePost, Long> {

    List<BlindSalePost> findByMemberId(Long memberId);

    List<BlindSalePost> findByBookId(Long bookId);
}
