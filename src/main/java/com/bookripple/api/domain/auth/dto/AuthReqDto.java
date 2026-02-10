package com.bookripple.api.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
  public static class RefreshToken {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
  }

  @Getter
  @NoArgsConstructor
  public static class Signup {

    @Schema(description = "로그인 아이디", example = "loginid")
    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @Schema(
        description = "비밀번호 (8자 이상, 영문/숫자/특수문자 중 2종 이상 포함)",
        example = "string123",
        type = "string"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^A-Za-z0-9])|(?=.*\\d)(?=.*[^A-Za-z0-9])).{8,}$",
        message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자 중 2종 이상을 포함해야 합니다."
    )
    private String password;

    @Schema(description = "사용자 이름", example = "북리플")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "이메일 주소", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "생년월일", example = "2000-01-01")
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @Schema(description = "필수 약관 동의 여부", example = "true")
    @AssertTrue(message = "필수 약관에 동의해야 합니다.")
    private Boolean isRequiredAgreed;

    @Schema(description = "선택 약관 동의 여부", example = "false")
    private Boolean isOptionalAgreed;
  }

  @Getter
  @NoArgsConstructor
  public static class Verify {

    private String email;
    private String code;
  }

  @Getter
  @NoArgsConstructor
  public static class PasswordReset {

    private String email;
    @Schema(
        description = "비밀번호 (8자 이상, 영문/숫자/특수문자 중 2종 이상 포함)",
        example = "string123",
        type = "string"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?:(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[^A-Za-z0-9])|(?=.*\\d)(?=.*[^A-Za-z0-9])).{8,}$",
        message = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자 중 2종 이상을 포함해야 합니다."
    )
    private String newPassword;
  }
}

