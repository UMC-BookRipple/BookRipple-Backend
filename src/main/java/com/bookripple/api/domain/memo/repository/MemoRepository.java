package com.bookripple.api.domain.memo.repository;

import com.bookripple.api.domain.memo.entity.Memo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    Slice<Memo> findByBookIdAndIdLessThanOrderByIdDesc(Long bookId, Long cursor, Pageable pageable);
}
