package com.bookripple.api.domain.notification.repository;

import com.bookripple.api.domain.notification.entity.Notification;
import com.bookripple.api.domain.notification.enums.NotificationType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  Slice<Notification> findByReceiverIdOrderByIdDesc(Long receiverId, Pageable pageable);

  Slice<Notification> findByReceiverIdAndIdLessThanOrderByIdDesc(Long receiverId, Long lastId,
      Pageable pageable);

  boolean existsByReceiverIdAndNotificationTypeAndUrlAndCreatedAtAfter(
      Long receiverId,
      NotificationType notificationType,
      String url,
      LocalDateTime createdAt
  );

  boolean existsByReceiverIdAndNotificationTypeAndUrl(
      Long receiverId,
      NotificationType notificationType,
      String url
  );

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Notification n SET n.isRead = true " +
      "WHERE n.receiver.id = :receiverId AND n.isRead = false")
  void markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);
}
