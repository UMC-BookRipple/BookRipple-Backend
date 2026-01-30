package com.bookripple.api.domain.notification.converter;

import com.bookripple.api.domain.notification.dto.NotificationResDto.Item;
import com.bookripple.api.domain.notification.dto.NotificationResDto.NotificationList;
import com.bookripple.api.domain.notification.entity.Notification;
import java.util.List;

public class NotificationConverter {

  public static Item toItem(Notification notification) {
    return Item.builder()
        .notificationId(notification.getId())
        .content(notification.getContent())
        .url(notification.getUrl())
        .isRead(notification.getIsRead())
        .notificationType(notification.getNotificationType())
        .createdAt(notification.getCreatedAt())
        .build();
  }

  public static NotificationList toNotificationList(List<Item> notificationList, Long lastId,
      Boolean hasNext) {
    return NotificationList.builder()
        .notificationList(notificationList)
        .lastId(lastId)
        .hasNext(hasNext)
        .build();
  }
}