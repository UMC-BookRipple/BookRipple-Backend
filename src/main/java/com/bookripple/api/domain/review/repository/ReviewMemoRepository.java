  package com.bookripple.api.domain.review.repository;

  import com.bookripple.api.domain.member.entity.Member;
  import com.bookripple.api.domain.review.entity.Review;
  import com.bookripple.api.domain.review.entity.ReviewMemo;
  import org.springframework.data.domain.Pageable;
  import org.springframework.data.domain.Slice;
  import org.springframework.data.jpa.repository.JpaRepository;
  import org.springframework.data.jpa.repository.Query;
  import org.springframework.data.repository.query.Param;
  import org.springframework.stereotype.Repository;

  @Repository
  public interface ReviewMemoRepository extends JpaRepository<ReviewMemo, Long> {

    boolean existsByReviewAndMember(Review review, Member member);


    @Query("SELECT rm FROM ReviewMemo rm " +
        "JOIN FETCH rm.review r " +
        "JOIN FETCH rm.member " +
        "JOIN FETCH r.member " +
        "JOIN FETCH r.book b " +
        "WHERE rm.member.id = :memberId " +
        "AND (:lastBookTitle IS NULL OR " +
        "    b.title > :lastBookTitle OR " +
        "    (b.title = :lastBookTitle AND rm.id < :lastId)) " +
        "ORDER BY b.title ASC, rm.id DESC ")
    Slice<ReviewMemo> findMyReviewMemoByCursor(
        @Param("memberId") Long memberId,
        @Param("lastBookTitle") String lastBookTitle,
        @Param("lastId") Long lastId,
        Pageable pageable
    );
  }
