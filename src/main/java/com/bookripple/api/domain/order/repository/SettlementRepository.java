package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByTradeId(Long tradeId);
    void deleteByTradeId(Long tradeId);
}
