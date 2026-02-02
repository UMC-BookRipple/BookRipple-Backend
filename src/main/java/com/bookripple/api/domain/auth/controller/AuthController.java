package com.bookripple.api.domain.auth.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.auth.service.AuthService;
import com.bookripple.api.global.dto.GlobalDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
   * 게스트 로그인 Stub
   */
  @PostMapping("/login/guest")
  public ResponseEntity<ApiResponse<AuthResDto.Login>> guestLogin() {
    AuthResDto.Login result = AuthResDto.Login.builder()
        .memberId(0L)
        .accessToken("GUEST_TOKEN")
        .build();

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
   * 로그아웃 Stub
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout() {
    return ResponseEntity.ok(
        ApiResponse.onSuccess(CommonSuccessCode.OK, "로그아웃 되었습니다.")
    );
  }
}
