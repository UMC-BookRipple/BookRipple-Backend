package com.bookripple.api.domain.library.repository;

import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    List<LibraryItem> findByMemberIdAndStatusAndIdLessThanOrderByIdDesc(
            Long memberId, LibraryStatus status, Long lastId, Pageable pageable
    );

    List<LibraryItem> findByMemberIdAndStatusOrderByIdDesc(
            Long memberId, LibraryStatus status, Pageable pageable
    );

    Optional<LibraryItem> findByMemberIdAndBook_Id(Long memberId, Long bookId);

    long deleteByMemberIdAndStatusAndBook_IdIn(
            Long memberId, LibraryStatus status, List<Long> bookIds
    );

    @Query("""
  select li.book.aladinBookId
  from LibraryItem li
  where li.member.id = :memberId
    and li.status in :status
    and li.book.aladinBookId in :aladinBookIds
""")
    List<Long> findRegisteredAladinBookIds(
            Long memberId,
            List<LibraryStatus> status,
            List<Long> aladinBookIds
    );
}

