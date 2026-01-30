package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.util.EmailSender;
import com.bookripple.api.domain.auth.util.VerificationCodeStore;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.domain.verification.email.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

  private final EmailSender emailSender;
  private final VerificationCodeStore codeStore;
  private final EmailVerificationService emailVerificationService;

  public void sendVerificationCode(String email, EmailVerificationPurpose purpose) {
    String code = generateCode();
    String key = generateKey(email, purpose);

    emailSender.send(email, "BookRipple 인증코드", "인증코드: " + code);
    codeStore.save(key, code);

    emailVerificationService.createOrRefresh(email, purpose);
  }

  public void verifyCode(String email, String inputCode, EmailVerificationPurpose purpose) {
    String key = generateKey(email, purpose);

    String savedCode = codeStore.get(key)
        .orElseThrow(() -> new ApiException(
            CommonErrorCode.NOT_FOUND,
            "인증코드를 찾을 수 없습니다."
        ));

    if (!savedCode.equals(inputCode)) {
      throw new ApiException(
          CommonErrorCode.BAD_REQUEST,
          "인증코드가 일치하지 않습니다."
      );
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
