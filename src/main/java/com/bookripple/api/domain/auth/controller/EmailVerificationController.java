package com.bookripple.api.domain.auth.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.service.EmailCodeService;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {

  private final EmailCodeService emailCodeService;

  /**
   * 회원가입용 이메일 인증 코드 발송
   */
  @PostMapping("/send")
  public ResponseEntity<ApiResponse<String>> sendVerificationCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    emailCodeService.sendVerificationCode(
        request.content(),
        EmailVerificationPurpose.SIGN_UP
    );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "인증코드가 발송되었습니다.")
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
