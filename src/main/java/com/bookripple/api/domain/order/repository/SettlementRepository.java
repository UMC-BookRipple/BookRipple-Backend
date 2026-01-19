package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
