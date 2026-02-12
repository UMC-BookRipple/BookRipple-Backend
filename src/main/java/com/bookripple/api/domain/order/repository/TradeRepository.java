package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<Trade> findByBuyerIdAndBlindSalePostId(Long buyerId, Long blindSalePostId);
}
