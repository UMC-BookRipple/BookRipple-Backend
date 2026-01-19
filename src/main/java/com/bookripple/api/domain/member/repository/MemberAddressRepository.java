package com.bookripple.api.domain.member.repository;

import com.bookripple.api.domain.order.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAddressRepository extends JpaRepository<Trade, Long> {
}
