package com.bookripple.api.domain.notification.service;


import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.notification.code.NotificationErrorCode;
import com.bookripple.api.domain.notification.entity.Notification;
import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.repository.NotificationRepository;
import com.bookripple.api.global.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Override
  @Transactional
  public void create(Member receiver, NotificationType notificationType, String content,
      String url) {
    Notification notification = Notification.builder()
        .receiver(receiver)
        .notificationType(notificationType)
        .content(content)
        .url(url)
        .isRead(false)
        .build();

    notificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void read(Long memberId, Long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ApiException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

    if (!notification.getReceiver().getId().equals(memberId)) {
      throw new ApiException(NotificationErrorCode.NOTIFICATION_FORBIDDEN);
    }

    if (!notification.getIsRead()) {
      notification.markRead();
    }
  }

  @Override
  @Transactional
  public void readAll(Long memberId) {
    notificationRepository.markAllAsReadByReceiverId(memberId);
  }
}