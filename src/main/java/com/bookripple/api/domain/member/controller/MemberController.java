package com.bookripple.api.domain.member.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  /**
   * 현재 비밀번호 확인 (Stub)
   */
  @PostMapping("/me/password/check")
  public ResponseEntity<ApiResponse<String>> checkCurrentPassword(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            "비밀번호가 확인되었습니다."
        )
    );
  }

  /**
   * 아이디 중복 확인 (Stub)
   */
  @GetMapping("/check-id")
  public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(
      @RequestParam("loginId") String loginId
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            true
        )
    );
  }

  /**
   * 아이디 최종 변경 (Stub)
   */
  @PatchMapping("/me/login-id")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> changeLoginId(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(1L)
        )
    );
  }

  /**
   * 이메일 변경 인증코드 발송 (Stub)
   */
  @PostMapping("/email/send")
  public ResponseEntity<ApiResponse<String>> sendEmailChangeCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            "인증코드가 발송되었습니다."
        )
    );
  }

  /**
   * 이메일 변경 인증코드 검증 (Stub)
   */
  @PostMapping("/email/verify")
  public ResponseEntity<ApiResponse<String>> verifyEmailChangeCode(
      @RequestBody @Valid AuthReqDto.Verify request
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            "이메일 인증이 완료되었습니다."
        )
    );
  }

  /**
   * 비밀번호 변경 (Stub)
   */
  @PutMapping("/me/password")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> changePassword(
      @RequestBody @Valid AuthReqDto.PasswordReset request
  ) {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(1L)
        )
    );
  }

  /**
   * 회원 탈퇴 (Stub)
   */
  @DeleteMapping("/me")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> withdrawMember() {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(1L)
        )
    );
  }
}
