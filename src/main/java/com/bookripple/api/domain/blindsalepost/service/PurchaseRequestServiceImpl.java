package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.code.PurchaseRequestErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.blindsalepost.converter.PurchaseRequestConverter;
import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;
import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import com.bookripple.api.domain.blindsalepost.repository.BlindSalePostRepository;
import com.bookripple.api.domain.blindsalepost.repository.PurchaseRequestRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.service.NotificationService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

  private static final String TRADE_REQUESTED_CONTENT = "구매 요청이 왔습니다. 요청을 승인하겠습니까?";
  private static final String TRADE_CANCELED_CONTENT = "구매 요청이 취소되었습니다.";
  private static final String TRADE_APPROVED_CONTENT = "구매 요청이 승인 되었습니다.";
  private static final String TRADE_REJECTED_CONTENT = "구매 요청이 거절되었습니다.";
  private static final String SHIPPING_STARTED_CONTENT = "상품이 배송중입니다.";
  private static final String SETTLEMENT_DONE_CONTENT = "거래가 완료되었습니다.";

  private final BlindSalePostRepository blindSalePostRepository;
  private final PurchaseRequestRepository purchaseRequestRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public PurchaseRequestResDto.Create createPurchaseRequest(Long memberId, Long blindSalePostId) {
    BlindSalePost blindSalePost = blindSalePostRepository.findById(blindSalePostId)
        .orElseThrow(() -> new ApiException(PurchaseRequestErrorCode.BLIND_SALE_POST_NOT_FOUND));

    Member buyer = memberRepository.findById(memberId)
        .orElseThrow(() -> new ApiException(MemberErrorCode.NO_MEMBER));

    PurchaseRequest purchaseRequest = PurchaseRequest.builder()
        .blindSalePost(blindSalePost)
        .member(buyer)
        .status(PurchaseStatus.WAITING)
        .build();

    purchaseRequestRepository.save(purchaseRequest);

    notificationService.create(
        blindSalePost.getMember(),
        NotificationType.TRADE_REQUESTED,
        TRADE_REQUESTED_CONTENT,
        toBlindSalePostUrl(blindSalePostId)
    );

    return PurchaseRequestConverter.toCreate(purchaseRequest);
  }

  @Override
  @Transactional
  public PurchaseRequestResDto.Decision cancelPurchaseRequest(Long memberId,
      Long purchaseRequestId) {
    PurchaseRequest purchaseRequest = getPurchaseRequest(purchaseRequestId);
    validateBuyer(memberId, purchaseRequest);
    validateStatus(purchaseRequest, PurchaseStatus.WAITING);

    purchaseRequest.updateStatus(PurchaseStatus.CANCELED);

    notificationService.create(
        purchaseRequest.getBlindSalePost().getMember(),
        NotificationType.TRADE_CANCELED,
        TRADE_CANCELED_CONTENT,
        toBlindSalePostUrl(purchaseRequest.getBlindSalePost().getId())
    );

    return PurchaseRequestConverter.toDecision(purchaseRequest);
  }


  private PurchaseRequest getPurchaseRequest(Long purchaseRequestId) {
    return purchaseRequestRepository.findById(purchaseRequestId)
        .orElseThrow(
            () -> new ApiException(PurchaseRequestErrorCode.PURCHASE_REQUEST_NOT_FOUND));
  }

  private void validateBuyer(Long memberId, PurchaseRequest purchaseRequest) {
    if (!purchaseRequest.getMember().getId().equals(memberId)) {
      throw new ApiException(PurchaseRequestErrorCode.BUYER_FORBIDDEN);
    }
  }

  private void validateStatus(PurchaseRequest purchaseRequest, PurchaseStatus... allowedStatus) {
    boolean match = Arrays.stream(allowedStatus)
        .anyMatch(status -> status == purchaseRequest.getStatus());
    if (!match) {
      throw new ApiException(PurchaseRequestErrorCode.INVALID_STATUS);
    }
  }

  private String toBlindSalePostUrl(Long blindSalePostId) {
    return "/blind-sale-posts/" + blindSalePostId;
  }
}
