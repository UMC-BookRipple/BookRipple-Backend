package com.bookripple.api.domain.member.controller;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.member.service.MemberService;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  private final MemberService memberService;

  /**
   * Helper: 현재 로그인한 사용자 ID 추가
   * SecurityContext에서 현재 로그인한 사용자의 ID(PK)를 꺼냄.
   */
  private Long getCurrentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
        authentication.getPrincipal() == null ||
        !(authentication.getPrincipal() instanceof Long)) {
      throw new ApiException(CommonErrorCode.UNAUTHORIZED);
    }

    return (Long) authentication.getPrincipal();
  }

  /**
   * 현재 비밀번호 확인
   */
  @PostMapping("/me/password/check")
  public ResponseEntity<ApiResponse<String>> checkCurrentPassword(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    // request.content() -> 사용자가 입력한 평문 비밀번호
    memberService.checkCurrentPassword(getCurrentMemberId(), request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "비밀번호가 확인되었습니다.")
    );
  }

  /**
   * 아이디 중복 확인 (수정용)
   */
  @GetMapping("/check-id")
  public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(
      @RequestParam("loginId") String loginId
  ) {
    // 중복이면 true, 사용 가능하면 false? -> 보통 API 명세에 따라 다름.
    // 기존 AuthController에서는 isAvailable 반환이었으므로, 여기서는 중복 여부를 반환하거나 사용 가능 여부를 반환.
    // 여기서는 "중복이 아니면 true(사용가능)" 로직으로 맞춤 (AuthController 참조)
    boolean isDuplicate = memberService.isLoginIdDuplicate(loginId);

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, !isDuplicate)
    );
  }

  /**
   * 아이디 최종 변경
   */
  @PatchMapping("/me/login-id")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> changeLoginId(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    // request.content() -> 변경할 새로운 Login ID
    Long memberId = memberService.changeLoginId(getCurrentMemberId(), request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, new GlobalDto.IdRes(memberId))
    );
  }

  /**
   * 비밀번호 변경
   */
  @PutMapping("/me/password")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> changePassword(
      @RequestBody @Valid AuthReqDto.PasswordReset request
  ) {
    // request.getNewPassword() -> 변경할 새로운 비밀번호
    // 주의: PasswordReset DTO에 email 필드가 있어도, 로그인된 상태이므로 무시하고 ID 기반 처리
    Long memberId = memberService.changePassword(getCurrentMemberId(), request.getNewPassword());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, new GlobalDto.IdRes(memberId))
    );
  }

  /**
   * 이메일 변경 인증코드 발송
   */
  @PostMapping("/email/send")
  public ResponseEntity<ApiResponse<String>> sendEmailChangeCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    // request.content() -> 변경하고자 하는 새로운 이메일
    memberService.sendEmailChangeCode(getCurrentMemberId(), request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "인증코드가 발송되었습니다.")
    );
  }

  /**
   * 이메일 변경 인증코드 검증 및 변경
   */
  @PostMapping("/email/verify")
  public ResponseEntity<ApiResponse<String>> verifyEmailChangeCode(
      @RequestBody @Valid AuthReqDto.Verify request
  ) {
    // 검증 성공 시 바로 이메일 변경 처리
    memberService.verifyAndChangeEmail(
        getCurrentMemberId(),
        request.getEmail(), // 검증하려는 이메일 (변경 대상)
        request.getCode()
    );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "이메일 변경이 완료되었습니다.")
    );
  }

  /**
   * 회원 탈퇴
   */
  @DeleteMapping("/me")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> withdrawMember() {
    Long memberId = getCurrentMemberId();
    memberService.withdraw(memberId);

    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(memberId)
        )
    );
  }
}