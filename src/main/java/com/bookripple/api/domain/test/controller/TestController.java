package com.bookripple.api.domain.test.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

  @GetMapping("/api/v1/test/me")
  public MeResponse me(Authentication authentication) {
    return new MeResponse((Long) authentication.getPrincipal());
  }

  @Getter
  public static class MeResponse {

    private final Long memberId;

    public MeResponse(Long memberId) {
      this.memberId = memberId;
    }
  }
}
