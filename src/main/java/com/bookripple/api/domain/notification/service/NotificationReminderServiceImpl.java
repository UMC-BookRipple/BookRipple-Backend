package com.bookripple.api.domain.notification.service;

import com.bookripple.api.domain.notification.enums.NotificationType;
import com.bookripple.api.domain.notification.repository.NotificationRepository;
import com.bookripple.api.domain.reading.entity.ReadingProgress;
import com.bookripple.api.domain.reading.repository.ReadingProgressRepository;
import com.bookripple.api.domain.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationReminderServiceImpl implements NotificationReminderService{

  private static final String READING_REMIND_CONTENT = "3일 동안 독서 기록이 없어요. 다시 이어서 읽어볼까요?";
  private static final String REVIEW_REMIND_CONTENT = "완독 축하해요! 감상평을 남겨보세요";

  private final ReadingProgressRepository readingProgressRepository;
  private final ReviewRepository reviewRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationService notificationService;

  @Override
  @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
  @Transactional
  public void sendReadingInactivityReminders() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = now.minusDays(3);
    LocalDateTime dedupThreshold = now.minusHours(24);

    List<ReadingProgress> targets = readingProgressRepository
        .findAllByIsCompletedFalseAndUpdatedAtBefore(threshold);

    for (ReadingProgress readingProgress : targets) {
      Long receiverId = readingProgress.getMember().getId();
      String url = toBookUrl(readingProgress.getBook().getId());

      boolean alreadySentToday = notificationRepository
          .existsByReceiverIdAndNotificationTypeAndUrlAndCreatedAtAfter(
              receiverId,
              NotificationType.READING_REMIND,
              url,
              dedupThreshold
          );

      if (alreadySentToday) {
        continue;
      }

      notificationService.create(
          readingProgress.getMember(),
          NotificationType.READING_REMIND,
          READING_REMIND_CONTENT,
          url
      );
    }
  }

  @Override
  @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
  @Transactional
  public void sendReviewWriteReminders() {
    LocalDateTime threshold = LocalDateTime.now().minusDays(1);

    List<ReadingProgress> targets = readingProgressRepository
        .findAllByIsCompletedTrueAndUpdatedAtBefore(threshold);

    for (ReadingProgress readingProgress : targets) {
      Long memberId = readingProgress.getMember().getId();
      Long bookId = readingProgress.getBook().getId();
      String url = toBookUrl(bookId);

      boolean hasReview = reviewRepository.existsByMemberIdAndBookId(memberId, bookId);
      if (hasReview) {
        continue;
      }

      boolean alreadySent = notificationRepository
          .existsByReceiverIdAndNotificationTypeAndUrl(
              memberId,
              NotificationType.REVIEW_REMIND,
              url
          );

      if (alreadySent) {
        continue;
      }

      notificationService.create(
          readingProgress.getMember(),
          NotificationType.REVIEW_REMIND,
          REVIEW_REMIND_CONTENT,
          url
      );
    }
  }

  private String toBookUrl(Long bookId) {
    return "/books/" + bookId;
  }
}
