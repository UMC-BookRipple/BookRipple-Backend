package com.bookripple.api.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthReqDto {

  @Getter
  @NoArgsConstructor
  public static class Login {

    private String loginId;
    private String password;
  }
}
