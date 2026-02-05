package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.AuthErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.util.EmailSender;
import com.bookripple.api.domain.auth.util.VerificationCodeStore;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.domain.verification.email.service.EmailVerificationService;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

  private final EmailSender emailSender;
  private final VerificationCodeStore codeStore;
  private final EmailVerificationService emailVerificationService;

  public LocalDateTime sendVerificationCode(String email, EmailVerificationPurpose purpose) {
    String code = generateCode();
    String key = generateKey(email, purpose);

    emailSender.send(email, "BookRipple 인증코드", "인증코드: " + code);
    codeStore.save(key, code);

    return emailVerificationService.createOrRefresh(email, purpose);
  }

  public void verifyCode(String email, String inputCode, EmailVerificationPurpose purpose) {
    String key = generateKey(email, purpose);

    String savedCode = codeStore.get(key)
        // 시간 만료로 삭제되었거나, 요청한 적 없음
        .orElseThrow(() -> new ApiException(AuthErrorCode.NOT_FOUND_VERIFICATION_CODE));

    if (!savedCode.equals(inputCode)) {
      throw new ApiException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    emailVerificationService.markVerified(email, purpose);
    codeStore.remove(key);
  }

  private String generateKey(String email, EmailVerificationPurpose purpose) {
    return email + ":" + purpose.name();
  }

  private String generateCode() {
    return String.format("%06d", new Random().nextInt(1_000_000));
  }
}