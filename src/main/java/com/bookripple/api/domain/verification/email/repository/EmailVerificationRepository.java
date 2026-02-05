package com.bookripple.api.domain.verification.email.repository;

import com.bookripple.api.domain.verification.email.entity.EmailVerification;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository
    extends JpaRepository<EmailVerification, Long> {

  /**
   * 이메일 + 목적 기준 인증 정보 조회
   */
  Optional<EmailVerification> findByEmailAndPurpose(
      String email,
      EmailVerificationPurpose purpose
  );
}
