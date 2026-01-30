package com.bookripple.api.domain.verification.email.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.verification.email.entity.EmailVerification;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.domain.verification.email.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

  private final EmailVerificationRepository emailVerificationRepository;

  @Transactional
  public void createOrRefresh(String email, EmailVerificationPurpose purpose) {
    emailVerificationRepository.findByEmailAndPurpose(email, purpose)
        .ifPresentOrElse(
            ev -> ev.refresh(LocalDateTime.now().plusMinutes(5)),
            () -> emailVerificationRepository.save(
                EmailVerification.builder()
                    .email(email)
                    .purpose(purpose)
                    .verified(false)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .build()
            )
        );
  }


  @Transactional
  public void markVerified(String email, EmailVerificationPurpose purpose) {
    EmailVerification verification =
        emailVerificationRepository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ApiException(
                CommonErrorCode.NOT_FOUND,
                "이메일 인증 요청이 존재하지 않습니다."
            ));

    verification.verify();
  }

  @Transactional(readOnly = true)
  public void validateVerified(String email, EmailVerificationPurpose purpose) {
    EmailVerification verification =
        emailVerificationRepository.findByEmailAndPurpose(email, purpose)
            .orElseThrow(() -> new ApiException(
                CommonErrorCode.FORBIDDEN,
                "이메일 인증이 필요합니다."
            ));

    if (!verification.isValid()) {
      throw new ApiException(
          CommonErrorCode.FORBIDDEN,
          "이메일 인증이 완료되지 않았거나 만료되었습니다."
      );
    }
  }
}
