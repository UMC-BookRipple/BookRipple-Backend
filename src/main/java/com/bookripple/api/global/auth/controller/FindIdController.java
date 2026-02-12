package com.bookripple.api.global.auth.controller;

import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.auth.dto.AuthReqDto;
import com.bookripple.api.global.auth.service.AuthService;
import com.bookripple.api.global.auth.service.EmailCodeService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/find-id")
@RequiredArgsConstructor
public class FindIdController {

  private final EmailCodeService emailCodeService;
  private final AuthService authService;

  @PostMapping("/email/send")
  public ResponseEntity<ApiResponse<String>> sendCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    emailCodeService.sendVerificationCode(
        request.content(),
        EmailVerificationPurpose.FIND_ID
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
        EmailVerificationPurpose.FIND_ID
    );

    String loginId = authService.findLoginIdByEmail(request.getEmail());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, loginId)
    );
  }
}