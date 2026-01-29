package com.bookripple.api.domain.notification.service;

import com.bookripple.api.domain.notification.dto.NotificationResDto.NotificationList;

public interface NotificationQueryService {

  NotificationList getNotifications(Long memberId, Long lastId, int size);
}
