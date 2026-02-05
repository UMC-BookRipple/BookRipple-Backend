package com.bookripple.api.domain.order.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.member.entity.MemberAddress;
import com.bookripple.api.domain.member.repository.MemberAddressRepository;
import com.bookripple.api.domain.order.converter.TradeConverter;
import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.entity.Payment;
import com.bookripple.api.domain.order.entity.Settlement;
import com.bookripple.api.domain.order.entity.Trade;
import com.bookripple.api.domain.order.entity.TradeShippingAddress;
import com.bookripple.api.domain.order.enums.PaymentStatus;
import com.bookripple.api.domain.order.enums.SettlementStatus;
import com.bookripple.api.domain.order.repository.PaymentRepository;
import com.bookripple.api.domain.order.repository.SettlementRepository;
import com.bookripple.api.domain.order.repository.TradeRepository;
import com.bookripple.api.domain.order.repository.TradeShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final PaymentRepository paymentRepository;
    private final SettlementRepository settlementRepository;

    @Override
    @Transactional
    public void preparePayment(Long memberId, Long tradeId, TradeReqDto.PreparePayment dto) {
        // 1. 거래 조회 및 권한 확인
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new RuntimeException("거래를 찾을 수 없습니다."));

        if (!trade.getBuyer().getId().equals(memberId)) {
            throw new RuntimeException("구매 권한이 없습니다.");
        }

        // 2. 한 줄 주소 업데이트
        trade.updateShippingAddress(dto.address());

        // 3. 컨버터를 통해 엔티티 생성 및 저장
        Payment payment = TradeConverter.toPayment(trade, dto);
        paymentRepository.save(payment);

        Settlement settlement = TradeConverter.toSettlement(trade);
        settlementRepository.save(settlement);
    }
}