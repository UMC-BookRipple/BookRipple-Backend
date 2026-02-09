package com.bookripple.api.domain.memo.repository;

import com.bookripple.api.domain.memo.entity.Memo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    Slice<Memo> findByBookIdAndIdLessThanOrderByIdDesc(Long bookId, Long cursor, Pageable pageable);
    Slice<Memo> findByMemberIdAndIdLessThanOrderByIdDesc(Long memberId, Long cursor, Pageable pageable);
    Slice<Memo> findByBookIdAndMemberIdAndIdLessThanOrderByIdDesc(
            Long bookId, Long memberId, Long cursor, Pageable pageable
    );
}
