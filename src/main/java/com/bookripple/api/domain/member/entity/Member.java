package com.bookripple.api.domain.member.entity;

import com.bookripple.api.global.entity.BaseEntity;
import com.bookripple.api.domain.member.enums.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String loginId;

  @Column(unique = true, length = 50)
  private String providerId; // KAKAO user id

  @Column(columnDefinition = "TEXT")
  private String password; // 소셜 로그인 시 NULL 가능

  @Column(nullable = false, length = 20)
  private String name;

  @Column(unique = true, length = 100)
  private String email;

  @Column
  private LocalDate birthDate;

  @Builder.Default
  @Column(nullable = false)
  private Boolean isCertified = false;  // 가입 이후에도 유지되는 이메일 인증 상태

  @Column(nullable = false)
  private Boolean isRequiredAgreed; // 필수약관동의 여부

  @Column
  private LocalDateTime requiredAgreedAt;

  @Column(nullable = false)
  private Boolean isOptionalAgreed; // 선택약관동의 여부

  @Column
  private LocalDateTime optionalAgreedAt;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberStatus status = MemberStatus.ACTIVE; // ACTIVE, SLEEP, QUIT

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType loginType = LoginType.LOCAL; // LOCAL, KAKAO, GUEST

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberRole role = MemberRole.USER; // USER, ADMIN

  public void certify() {
    this.isCertified = true;
  }
  //회원가입 시 → true
  //이메일 변경 시 → false
  //재인증 후 → true

  // 비밀번호 변경
  public void updatePassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  // 로그인 아이디 변경
  public void updateLoginId(String newLoginId) {
    this.loginId = newLoginId;
  }

  // 이메일 변경 (변경 시 인증 상태 초기화)
  public void updateEmail(String newEmail) {
    this.email = newEmail;
    this.isCertified = false; // 이메일이 바뀌었으므로 재인증 필요 시 false로 설정
  }

  // 회원 탈퇴(Soft Delete)
  public void withdraw() {
    if (this.status == MemberStatus.QUIT) {
      return; // 이미 탈퇴한 경우
    }

    this.status = MemberStatus.QUIT;
    this.isCertified = false;

    // Unique Key 제약 충돌 방지 & 개인정보 마스킹
    // 예: "user1" -> "deleted_1700000000_user1"
    long timestamp = System.currentTimeMillis();

    this.loginId = "deleted_" + timestamp + "_" + this.loginId;
    this.email = "deleted_" + timestamp + "_" + this.email;

    // 개인정보 삭제
    this.name = "탈퇴회원"; // 또는 기존 이름 유지 정책에 따라 결정
    this.providerId = null; // 소셜 연동 해제
    this.password = null;   // 비밀번호 삭제


    // 약관 동의 정보 초기화
    this.isRequiredAgreed = false;
    this.isOptionalAgreed = false;
  }

}
