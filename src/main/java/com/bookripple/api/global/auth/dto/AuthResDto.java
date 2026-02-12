package com.bookripple.api.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthResDto {

  @Getter
  @Builder
  public static class Login {

    private Long memberId;
    private String userName;
    private String accessToken;
    private String refreshToken;
    private Boolean isNewMember;
  }

  @Getter
  @Builder
  public static class TokenRefresh {

    private String accessToken;
    private String refreshToken;
  }
}
