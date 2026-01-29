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

  @Column(columnDefinition = "TEXT")
  private String password; // 소셜 로그인 시 NULL 가능

  @Column(nullable = false, length = 20)
  private String name;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Builder.Default
  @Column(nullable = false)
  private Boolean isCertified = false;  // 이메일 인증 여부

  @Column(nullable = false)
  private Boolean isRequiredAgreed; // 필수약관동의 여부

  @Column(nullable = false)
  private Boolean isOptionalAgreed; // 선택약관동의 여부

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

}
