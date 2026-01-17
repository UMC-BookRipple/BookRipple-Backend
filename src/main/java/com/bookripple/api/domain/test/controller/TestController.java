package com.bookripple.api.domain.test.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
public class TestController {

  @GetMapping("/api/v1/test/me")
  public ResponseEntity<ApiResponse<MeResponse>> me(Authentication authentication) {
    // 원래는 Security에서 막혀서 null이 오면 안 되지만, 내부 테스트용으로 방어
    Long memberId = authentication == null ? null : (Long) authentication.getPrincipal();

    MeResponse result = new MeResponse(memberId);

    return ResponseEntity
        .status(CommonSuccessCode.OK.getHttpStatus())
        .body(ApiResponse.onSuccess(CommonSuccessCode.OK, result));
  }

  public record MeResponse(Long memberId) {}
}
