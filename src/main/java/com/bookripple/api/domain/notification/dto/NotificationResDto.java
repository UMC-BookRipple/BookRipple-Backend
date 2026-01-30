package com.bookripple.api.domain.notification.dto;


import com.bookripple.api.domain.notification.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class NotificationResDto {

  @Builder
  public record Item(
      Long notificationId,
      String content,
      String url,
      Boolean isRead,
      NotificationType notificationType,
      LocalDateTime createdAt
  ) {

  }

  @Builder
  public record NotificationList(
      List<Item> notificationList,
      Long lastId,
      Boolean hasNext
  ) {

  }
}