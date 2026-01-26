package com.bookripple.api.domain.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthReqDto {

  @Getter
  @NoArgsConstructor
  public static class Login {

    @NotBlank(message = "아이디는 필수입니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
  }

  @Getter
  @NoArgsConstructor
  public static class Signup {

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^A-Za-z0-9])|(?=.*\\d)(?=.*[^A-Za-z0-9])).{8,}$",
        message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자 중 2종 이상을 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @AssertTrue(message = "필수 약관에 동의해야 합니다.")
    private Boolean isRequiredAgreed;

    private Boolean isOptionalAgreed;
  }

  @Getter
  @NoArgsConstructor
  public static class Verify {

    private String email;
    private String code;
  }
}

