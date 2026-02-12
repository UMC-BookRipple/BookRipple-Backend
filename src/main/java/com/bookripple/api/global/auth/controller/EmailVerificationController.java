package com.bookripple.api.global.auth.controller;

import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.auth.dto.AuthReqDto;
import com.bookripple.api.global.auth.service.EmailCodeService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {

  private final EmailCodeService emailCodeService;

  /**
   * 회원가입용 이메일 인증 코드 발송
   */
  @PostMapping("/send")
  public ResponseEntity<ApiResponse<GlobalDto.SingleRes<LocalDateTime>>> sendVerificationCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    LocalDateTime expiredAt =
        emailCodeService.sendVerificationCode(
            request.content(),
            EmailVerificationPurpose.SIGN_UP
        );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.SingleRes<>(expiredAt)
        )
    );
  }

  /**
   * 회원가입용 이메일 인증 코드 검증
   */
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<String>> verifyCode(
      @RequestBody @Valid AuthReqDto.Verify request
  ) {
    emailCodeService.verifyCode(
        request.getEmail(),
        request.getCode(),
        EmailVerificationPurpose.SIGN_UP
    );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "이메일 인증이 완료되었습니다.")
    );
  }
}
