package com.bookripple.api.domain.verification.email.entity;

import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
    name = "email_auth",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "purpose"})
    }
)
public class EmailVerification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EmailVerificationPurpose purpose;

  @Column(nullable = false)
  private boolean verified;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  public void verify() {
    this.verified = true;
  }

  public boolean isExpired() {
    return expiredAt.isBefore(LocalDateTime.now());
  }

  public boolean isValid() {
    return verified && !isExpired();
  }

  public void refresh(LocalDateTime expiredAt) {
    this.verified = false;
    this.expiredAt = expiredAt;
  }
}
