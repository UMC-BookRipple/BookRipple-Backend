package com.bookripple.api.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthResDto {

  @Getter
  @Builder
  public static class Login {

    private Long memberId;
    private String userName;
    private String accessToken;
  }
}
