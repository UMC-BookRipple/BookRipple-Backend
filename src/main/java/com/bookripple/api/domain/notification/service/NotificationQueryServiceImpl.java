package com.bookripple.api.domain.notification.service;

import com.bookripple.api.domain.notification.converter.NotificationConverter;
import com.bookripple.api.domain.notification.dto.NotificationResDto.Item;
import com.bookripple.api.domain.notification.dto.NotificationResDto.NotificationList;
import com.bookripple.api.domain.notification.entity.Notification;
import com.bookripple.api.domain.notification.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {

  private final NotificationRepository notificationRepository;

  @Override
  public NotificationList getNotifications(Long memberId, Long lastId, int size) {
    Pageable pageable = PageRequest.of(0, size);

    Slice<Notification> notificationSlice = (lastId == null)
        ? notificationRepository.findByReceiverIdOrderByIdDesc(memberId, pageable)
        : notificationRepository.findByReceiverIdAndIdLessThanOrderByIdDesc(memberId, lastId,
            pageable);

    List<Item> items = notificationSlice.stream()
        .map(NotificationConverter::toItem)
        .toList();

    Long nextId = items.isEmpty() ? null : items.get(items.size() - 1).notificationId();

    return NotificationConverter.toNotificationList(items, nextId, notificationSlice.hasNext());
  }
}
