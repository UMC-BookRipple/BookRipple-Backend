package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Trade, Long> {
}
