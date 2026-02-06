package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.code.PurchaseRequestErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.blindsalepost.converter.PurchaseRequestConverter;
import com.bookripple.api.domain.blindsalepost.dto.PurchaseRequestResDto;
import com.bookripple.api.domain.blindsalepost.entity.BlindSalePost;
import com.bookripple.api.domain.blindsalepost.entity.PurchaseRequest;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;
import com.bookripple.api.domain.blindsalepost.enums.PurchaseStatus;
import com.bookripple.api.domain.blindsalepost.repository.BlindSalePostRepository;
import com.bookripple.api.domain.blindsalepost.repository.PurchaseRequestRepository;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.service.NotificationService;
import java.util.Arrays;

import com.bookripple.api.domain.order.entity.Trade;
import com.bookripple.api.domain.order.enums.TradeStatus;
import com.bookripple.api.domain.order.repository.TradeRepository;
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


  private final BlindSalePostRepository blindSalePostRepository;
  private final PurchaseRequestRepository purchaseRequestRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;
  private final TradeRepository tradeRepository;

  @Override
  @Transactional
  public PurchaseRequestResDto.Create createPurchaseRequest(Long memberId, Long blindSalePostId) {
    BlindSalePost blindSalePost = blindSalePostRepository.findById(blindSalePostId)
        .orElseThrow(() -> new ApiException(PurchaseRequestErrorCode.BLIND_SALE_POST_NOT_FOUND));

    Member buyer = memberRepository.findById(memberId)
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

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

  @Override
  @Transactional
  public PurchaseRequestResDto.Decision approvePurchaseRequest(Long memberId,
      Long purchaseRequestId) {
    PurchaseRequest purchaseRequest = getPurchaseRequest(purchaseRequestId);
    validateSeller(memberId, purchaseRequest);
    validateStatus(purchaseRequest, PurchaseStatus.WAITING);

    purchaseRequest.updateStatus(PurchaseStatus.ACCEPTED);

    //  결제 로직: 게시글 예약 및 거래 생성
    BlindSalePost post = purchaseRequest.getBlindSalePost();
    post.updateStatus(PostStatus.RESERVED); // 게시글 잠금

    Trade trade = Trade.builder() // 거래 생성
            .buyer(purchaseRequest.getMember())
            .seller(post.getMember())
            .blindSalePost(post)
            .amount(post.getPrice())
            .status(TradeStatus.REQUESTED) // 기본값으로 설정됨
            .build();
    tradeRepository.save(trade);

    // 나머지 대기자들 일괄 거절 처리
    purchaseRequestRepository.rejectOthers(post.getId(), purchaseRequestId);

    notificationService.create(
        purchaseRequest.getMember(),
        NotificationType.TRADE_APPROVED,
        TRADE_APPROVED_CONTENT,
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

  private void validateSeller(Long memberId, PurchaseRequest purchaseRequest) {
    if (!purchaseRequest.getBlindSalePost().getMember().getId().equals(memberId)) {
      throw new ApiException(PurchaseRequestErrorCode.SELLER_FORBIDDEN);
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
