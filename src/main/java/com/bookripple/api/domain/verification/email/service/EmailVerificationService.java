package com.bookripple.api.domain.verification.email.service;

import com.bookripple.api.domain.verification.email.entity.EmailVerification;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.domain.verification.email.repository.EmailVerificationRepository;
import com.bookripple.api.global.auth.code.AuthErrorCode;
import com.bookripple.api.global.error.ApiException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

  private final EmailVerificationRepository emailVerificationRepository;

  @Transactional
  public LocalDateTime createOrRefresh(String email, EmailVerificationPurpose purpose) {
    LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(2);

    emailVerificationRepository.findByEmailAndPurpose(email, purpose)
        .ifPresentOrElse(
            ev -> ev.refresh(expiredAt),
            () -> emailVerificationRepository.save(
                EmailVerification.builder()
                    .email(email)
                    .purpose(purpose)
                    .verified(false)
                    .expiredAt(expiredAt)
                    .build()
            )
        );

    return expiredAt;
  }

  @Transactional
  public void markVerified(String email, EmailVerificationPurpose purpose) {
    EmailVerification verification =
        emailVerificationRepository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ApiException(AuthErrorCode.NOT_FOUND_VERIFICATION_CODE));

    if (verification.isExpired()) {
      throw new ApiException(AuthErrorCode.EXPIRED_VERIFICATION_CODE);
    }
    verification.confirmVerification(LocalDateTime.now().plusMinutes(30));
  }

  @Transactional(readOnly = true)
  public void validateVerified(String email, EmailVerificationPurpose purpose) {
    EmailVerification verification =
        emailVerificationRepository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ApiException(AuthErrorCode.EMAIL_NOT_VERIFIED));

    if (!verification.isValid()) {
      throw new ApiException(AuthErrorCode.EMAIL_NOT_VERIFIED);
    }
  }
}