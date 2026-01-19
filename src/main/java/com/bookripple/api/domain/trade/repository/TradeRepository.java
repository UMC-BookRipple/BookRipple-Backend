package com.bookripple.api.domain.trade.repository;

import com.bookripple.api.domain.trade.entity.Trade;
import com.bookripple.api.domain.trade.enums.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

}
