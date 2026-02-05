package com.bookripple.api.domain.order.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.PurchaseRequestErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import com.bookripple.api.domain.blindsalepost.repository.PurchaseRequestRepository;
import com.bookripple.api.domain.member.entity.MemberAddress;
import com.bookripple.api.domain.member.repository.MemberAddressRepository;
import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.service.NotificationService;
import com.bookripple.api.domain.order.converter.TradeConverter;
import com.bookripple.api.domain.order.dto.TradeReqDto;
import com.bookripple.api.domain.order.entity.Payment;
import com.bookripple.api.domain.order.entity.Settlement;
import com.bookripple.api.domain.order.entity.Trade;
import com.bookripple.api.domain.order.entity.TradeShippingAddress;
import com.bookripple.api.domain.order.enums.PaymentStatus;
import com.bookripple.api.domain.order.enums.SettlementStatus;
import com.bookripple.api.domain.order.enums.TradeStatus;
import com.bookripple.api.domain.order.repository.PaymentRepository;
import com.bookripple.api.domain.order.repository.SettlementRepository;
import com.bookripple.api.domain.order.repository.TradeRepository;
import com.bookripple.api.domain.order.repository.TradeShippingAddressRepository;
import com.bookripple.api.domain.toss.client.TossPaymentClient;
import com.bookripple.api.domain.toss.dto.TossPaymentDto;
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
    private final TossPaymentClient tossPaymentClient;
    private final NotificationService notificationService;
    private final PurchaseRequestRepository purchaseRequestRepository;


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

    @Override
    @Transactional
    public void confirmPayment(Long memberId, Long tradeId, String paymentKey, String orderId, Integer amount) {
        // 1. 토스 서버에 실제 승인 요청
        TossPaymentDto.ConfirmResponse response = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);

        // 2. 거래 및 기존 데이터 조회
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();
        Payment payment = paymentRepository.findByTradeId(tradeId).orElseThrow();
        Settlement settlement = settlementRepository.findByTradeId(tradeId).orElseThrow();

        // 3. 결제(Payment) 정보 업데이트: READY -> DONE
        TradeConverter.updatePaymentSuccess(payment, response);
        paymentRepository.save(payment);

        // 4. 정산(Settlement) 정보 업데이트: PENDING -> COMPLETED
        settlement.updateStatus(SettlementStatus.COMPLETED);

        // 5. 연관 데이터 상태 일괄 변경 (사용자 로직 핵심)
        trade.updateStatus(TradeStatus.PAID); // 거래 완료
        trade.getBlindSalePost().updateStatus(PostStatus.SOLD_OUT); // 게시글 품절
        PurchaseRequest purchaseRequest = purchaseRequestRepository
                .findByBlindSalePostIdAndStatus(trade.getBlindSalePost().getId(), PurchaseStatus.ACCEPTED)
                .orElseThrow(() -> new ApiException(PurchaseRequestErrorCode.PURCHASE_REQUEST_NOT_FOUND));
        purchaseRequest.updateStatus(PurchaseStatus.PAYMENT_COMPLETED); // 거래 완료

        // 6. NotificationService.create 규격에 맞춰 알림 생성
        String notificationContent = String.format("결제가 완료되었습니다! 배송지: [%s]", trade.getShippingAddress());

        notificationService.create(
                trade.getSeller(),                  // 수신자: 판매자
                NotificationType.SETTLEMENT_DONE,    // 알림 타입
                notificationContent,                 // 내용: 한 줄 주소 포함
                "/trades/" + trade.getId()           // 이동할 URL
        );
    }


}