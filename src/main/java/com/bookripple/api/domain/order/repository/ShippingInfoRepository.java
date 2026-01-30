package com.bookripple.api.domain.order.repository;

import com.bookripple.api.domain.order.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {

}
