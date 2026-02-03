package com.bookripple.api.domain.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookripple.api.domain.reading.entity.ReadingRecord;

public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {
}
