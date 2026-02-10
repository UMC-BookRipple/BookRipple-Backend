package com.bookripple.api.domain.notification.service;

public interface NotificationReminderService {

  void sendReadingInactivityReminders();

  void sendReviewWriteReminders();

}
