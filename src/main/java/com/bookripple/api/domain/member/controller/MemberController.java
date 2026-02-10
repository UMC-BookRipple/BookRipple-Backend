package com.bookripple.api.domain.member.controller;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.service.EmailCodeService;
import com.bookripple.api.domain.member.dto.MemberReqDto;
import com.bookripple.api.domain.member.service.MemberService;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  private final MemberService memberService;
  private final EmailCodeService emailCodeService;

  /**
   * Helper 메서드 - 현재 로그인한 사용자 ID 추가
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
   * 아이디 중복 확인 (수정용)
   */
  @GetMapping("/check-id")
  public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(
      @RequestParam("loginId") String loginId
  ) {
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
    Long memberId = memberService.changeLoginId(getCurrentMemberId(), request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, new GlobalDto.IdRes(memberId))
    );
  }

  /**
   * 현재 비밀번호 확인
   */
  @PostMapping("/me/password/check")
  public ResponseEntity<ApiResponse<String>> checkCurrentPassword(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    memberService.checkCurrentPassword(getCurrentMemberId(), request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "비밀번호가 확인되었습니다.")
    );
  }

  /**
   * 비밀번호 변경
   */
  @PutMapping("/me/password")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> changePassword(
      @RequestBody @Valid MemberReqDto.PasswordUpdate request
  ) {
    Long memberId = memberService.changePassword(getCurrentMemberId(), request);

    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(memberId)
        )
    );
  }

  /**
   * 이메일 변경 인증코드 발송
   */
  @PostMapping("/email/send")
  public ResponseEntity<ApiResponse<GlobalDto.SingleRes<LocalDateTime>>> sendEmailChangeCode(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    LocalDateTime expiredAt =
        emailCodeService.sendVerificationCode(
            request.content(),
            EmailVerificationPurpose.CHANGE_EMAIL
        );

    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.SingleRes<>(expiredAt)
        )
    );
  }

  /**
   * 이메일 변경 인증코드 검증 및 변경
   */
  @PostMapping("/email/verify")
  public ResponseEntity<ApiResponse<String>> verifyEmailChangeCode(
      @RequestBody @Valid AuthReqDto.Verify request
  ) {
    memberService.verifyAndChangeEmail(
        getCurrentMemberId(),
        request.getEmail(),
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
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> withdrawMember(
      HttpServletRequest request,
      @RequestBody(required = false) AuthReqDto.RefreshToken refreshTokenRequest
  ) {
    Long memberId = getCurrentMemberId();
    String accessToken = resolveToken(request);
    String refreshToken = refreshTokenRequest != null ? refreshTokenRequest.getRefreshToken() : null;

    memberService.withdraw(memberId, accessToken, refreshToken);

    return ResponseEntity.ok(
        ApiResponse.onSuccess(
            CommonSuccessCode.OK,
            new GlobalDto.IdRes(memberId)
        )
    );
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}