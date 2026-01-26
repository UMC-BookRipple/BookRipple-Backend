package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.TradeShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeShippingAddressRepository extends JpaRepository<TradeShippingAddress, Long> {
}
