package com.bookripple.api.domain.book.repository;

public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

    void deleteByBookIdAndMemberId(Long bookId, Long memberId);
}

