package com.bookripple.api.domain.trade.repository;

import com.bookripple.api.domain.trade.entity.Trade;
import com.bookripple.api.domain.trade.enums.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerId(Long buyerId);

    List<Trade> findBySellerId(Long sellerId);

    List<Trade> findByStatus(TradeStatus status);

    List<Trade> findByBlindSalePostId(Long blindSalePostId);
}
