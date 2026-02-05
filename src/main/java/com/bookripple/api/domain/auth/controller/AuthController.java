package com.bookripple.api.domain.auth.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.auth.service.AuthService;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  /**
   * 로컬 로그인
   */
  @PostMapping("/login/local")
  public ResponseEntity<ApiResponse<AuthResDto.Login>> login(
      @RequestBody @Valid AuthReqDto.Login request) {

    AuthResDto.Login result = authService.localLogin(request);

    return ResponseEntity
        .status(CommonSuccessCode.OK.getHttpStatus())
        .body(ApiResponse.onSuccess(CommonSuccessCode.OK, result));
  }

  /**
   * 로컬 회원가입
   */
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<GlobalDto.IdRes>> signup(
      @RequestBody @Valid AuthReqDto.Signup request) {

    GlobalDto.IdRes result = authService.signup(request);

    return ResponseEntity
        .status(CommonSuccessCode.OK.getHttpStatus())
        .body(ApiResponse.onSuccess(CommonSuccessCode.OK, result));
  }

  /**
   * 아이디 중복 확인
   */
  @GetMapping("/check-id")
  public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(
      @RequestParam("loginId") String loginId
  ) {
    boolean isAvailable = authService.isLoginIdAvailable(loginId);
    return ResponseEntity.ok(ApiResponse.onSuccess(CommonSuccessCode.OK, isAvailable));
  }

  /**
   * 게스트 로그인
   */
  @PostMapping("/login/guest")
  public ResponseEntity<ApiResponse<AuthResDto.Login>> guestLogin() {
    AuthResDto.Login result = authService.guestLogin();

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, result)
    );
  }

  /**
   * 카카오 로그인
   */
  @PostMapping("/kakao")
  public ResponseEntity<ApiResponse<AuthResDto.Login>> kakaoLogin(
      @RequestBody @Valid GlobalDto.ContentReq request
  ) {
    AuthResDto.Login result = authService.kakaoLogin(request.content());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, result)
    );
  }

  /**
   * 카카오 Redirect URI 처리 (인가 코드 수신용)
   * 테스트를 위해 GET으로 열어둠.. 실제 서비스 시에는 프론트에서 코드를 받아 POST로 전달함
   *
  @GetMapping("/kakao/callback")
  public ResponseEntity<ApiResponse<AuthResDto.Login>> kakaoCallback(@RequestParam("code") String code) {
    AuthResDto.Login result = authService.kakaoLogin(code);
    return ResponseEntity.ok(ApiResponse.onSuccess(CommonSuccessCode.OK, result));
  }

  */

  /**
   * 로그아웃
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {

    // 1. 헤더에서 토큰 문자열 꺼내기
    String accessToken = resolveToken(request);

    // 2. 로그아웃 수행 (토큰이 있을 때만)
    if (accessToken != null) {
      authService.logout(accessToken);
    }

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "로그아웃 되었습니다.")
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
