package com.bookripple.api.domain.notification.service;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.notification.enums.NotificationType;

public interface NotificationService {

  void create(Member receiver, NotificationType notificationType, String content, String url);

  void read(Long memberId, Long notificationId);

  void readAll(Long memberId);
}