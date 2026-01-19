package com.bookripple.api.domain.auth.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthReqDto {

  @Getter
  @NoArgsConstructor
  public static class Login {

    private String loginId;
    private String password;
  }

  @Getter
  @NoArgsConstructor
  public static class Signup{

    private String loginId;
    private String password;
    private String name;
    private String email;
    private LocalDate birthDate;
    private Boolean isRequiredAgreed;
    private Boolean isOptionalAgreed;
  }
}

