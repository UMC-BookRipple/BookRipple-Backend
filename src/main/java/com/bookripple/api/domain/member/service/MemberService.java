package com.bookripple.api.domain.member.service;

import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.service.EmailCodeService;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailCodeService emailCodeService;

  /**
   * 현재 비밀번호 확인
   */
  @Transactional(readOnly = true)
  public void checkCurrentPassword(Long memberId, String rawPassword) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
      throw new ApiException(MemberErrorCode.PASSWORD_MISMATCH);
    }
  }

  /**
   * 아이디 중복 확인 (이미 사용 중인지)
   */
  @Transactional(readOnly = true)
  public boolean isLoginIdDuplicate(String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }

  /**
   * 로그인 아이디 최종 변경
   */
  @Transactional
  public Long changeLoginId(Long memberId, String newLoginId) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    if (memberRepository.existsByLoginId(newLoginId)) {
      throw new ApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);
    }

    member.updateLoginId(newLoginId);
    return member.getId();
  }

  /**
   * 비밀번호 변경
   */
  @Transactional
  public Long changePassword(Long memberId, String newPassword) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    String encodedPassword = passwordEncoder.encode(newPassword);
    member.updatePassword(encodedPassword);

    return member.getId();
  }

  /**
   * 이메일 변경 인증 코드 발송
   */
  @Transactional
  public void sendEmailChangeCode(Long memberId, String newEmail) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    // 자신의 현재 이메일과 동일한지 체크
    if (newEmail.equals(member.getEmail())) {
      throw new ApiException(MemberErrorCode.EMAIL_SAME_AS_OLD);
    }

    // 다른 사람이 이미 사용 중인 이메일인지 체크
    if (memberRepository.existsByEmail(newEmail)) {
      throw new ApiException(MemberErrorCode.DUPLICATE_EMAIL);
    }

    // 인증 코드 발송
    emailCodeService.sendVerificationCode(newEmail, EmailVerificationPurpose.CHANGE_EMAIL);
  }

  /**
   * 이메일 변경 인증 코드 검증 및 이메일 최종 수정
   */
  @Transactional
  public void verifyAndChangeEmail(Long memberId, String email, String code) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    emailCodeService.verifyCode(email, code, EmailVerificationPurpose.CHANGE_EMAIL);

    member.updateEmail(email);
    member.certify();
  }

  /**
   * 회원 탈퇴
   */
  @Transactional
  public void withdraw(Long memberId) {
    Member member = getMemberOrThrow(memberId);
    memberRepository.delete(member);
  }

  // --- Helper Methods ---

  private Member getMemberOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));
  }

  /**
   * 로컬 계정인지 검증
   */
  private void validateLocalAccount(Member member) {
    if (member.getLoginType() != LoginType.LOCAL) {
      throw new ApiException(MemberErrorCode.SOCIAL_PROFILE_RESTRICTION);
    }
  }
}