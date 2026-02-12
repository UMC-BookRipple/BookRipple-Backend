package com.bookripple.api.domain.notification.controller;

import com.bookripple.api.domain.notification.dto.NotificationResDto.NotificationList;
import com.bookripple.api.domain.notification.service.NotificationQueryService;
import com.bookripple.api.domain.notification.service.NotificationService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.response.ApiResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationQueryService notificationQueryService;
  private final NotificationService notificationService;

  @GetMapping
  public ApiResponse<NotificationList> getNotifications(
      @AuthenticationPrincipal Long memberId,
      @RequestParam(required = false) @Min(1) Long lastId,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
  ) {
    return ApiResponse.onSuccess(CommonSuccessCode.OK,
        notificationQueryService.getNotifications(memberId, lastId, size));
  }

  @PatchMapping("/{notificationId}/read")
  public ApiResponse<Void> readNotification(
      @AuthenticationPrincipal Long memberId,
      @PathVariable @Min(1) Long notificationId
  ) {
    notificationService.read(memberId, notificationId);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, null);
  }

  @PatchMapping("/read-all")
  public ApiResponse<Void> readAllNotifications(
      @AuthenticationPrincipal Long memberId
  ) {
    notificationService.readAll(memberId);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, null);
  }
}
