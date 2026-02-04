package com.bookripple.api.domain.auth.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.service.AuthService; // 추가
import com.bookripple.api.domain.auth.service.EmailCodeService;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/find-pw")
@RequiredArgsConstructor
public class FindPasswordController {

  private final EmailCodeService emailCodeService;
  private final AuthService authService;

  @PostMapping("/email/send")
  public ResponseEntity<ApiResponse<String>> sendCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    emailCodeService.sendVerificationCode(
        request.content(),
        EmailVerificationPurpose.FIND_PW
    );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "인증코드가 발송되었습니다.")
    );
  }

  @PostMapping("/email/verify")
  public ResponseEntity<ApiResponse<String>> verifyCode(
      @RequestBody @Valid AuthReqDto.Verify request
  ) {
    emailCodeService.verifyCode(
        request.getEmail(),
        request.getCode(),
        EmailVerificationPurpose.FIND_PW
    );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "이메일 인증이 완료되었습니다.")
    );
  }

  @PostMapping("/password/reset")
  public ResponseEntity<ApiResponse<String>> resetPassword(
      @RequestBody @Valid AuthReqDto.PasswordReset request
  ) {
    authService.resetPassword(request);

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "비밀번호가 변경되었습니다.")
    );
  }
}