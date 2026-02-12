package com.bookripple.api.global.auth.controller;

import com.bookripple.api.global.auth.dto.AuthReqDto;
import com.bookripple.api.global.auth.dto.AuthResDto;
import com.bookripple.api.global.auth.service.AuthService;
import com.bookripple.api.global.code.CommonSuccessCode;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   * 로그아웃
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(
      HttpServletRequest request,
      @RequestBody(required = false) AuthReqDto.RefreshToken refreshTokenRequest
  ) {

    String accessToken = resolveToken(request);

    if (accessToken != null) {
      String refreshToken =
          refreshTokenRequest != null ? refreshTokenRequest.getRefreshToken() : null;
      authService.logout(accessToken, refreshToken);
    }

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "로그아웃 되었습니다.")
    );
  }

  /**
   * AccessToken 재발급 (RefreshToken 사용)
   */
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResDto.TokenRefresh>> refresh(
      @RequestBody @Valid AuthReqDto.RefreshToken request
  ) {
    AuthResDto.TokenRefresh result = authService.refreshAccessToken(request.getRefreshToken());

    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, result)
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
