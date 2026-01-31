package com.bookripple.api.domain.library.repository;

import com.bookripple.api.domain.library.entity.LibraryItem;
import com.bookripple.api.domain.library.enums.LibraryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {

    List<LibraryItem> findByMemberIdAndStatusAndIdLessThanOrderByIdDesc(
            Long memberId, LibraryStatus status, Long lastId, Pageable pageable
    );

    List<LibraryItem> findByMemberIdAndStatusOrderByIdDesc(
            Long memberId, LibraryStatus status, Pageable pageable
    );
}

