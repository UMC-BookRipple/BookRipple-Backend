package com.bookripple.api.domain.member.service;

import com.bookripple.api.domain.member.code.MemberErrorCode;
import com.bookripple.api.domain.member.dto.MemberReqDto;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.enums.MemberStatus;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.auth.repository.RefreshTokenRepository;
import com.bookripple.api.global.auth.service.EmailCodeService;
import com.bookripple.api.global.error.ApiException;
import com.bookripple.api.global.security.JwtTokenProvider;
import com.bookripple.api.global.service.TokenBlacklistService;
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
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenRepository refreshTokenRepository;

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
  public Long changePassword(Long memberId, MemberReqDto.PasswordUpdate request) {
    Member member = getMemberOrThrow(memberId);

    validateLocalAccount(member);

    // 1. 새 비밀번호와 새 비밀번호 확인 일치 여부 검증
    if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
      throw new ApiException(MemberErrorCode.PASSWORD_CONFIRM_MISMATCH); // 에러 코드 필요
    }

    // 2. 현재 비밀번호 일치 여부 확인 (DB의 기존 비밀번호와 비교)
    if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
      throw new ApiException(MemberErrorCode.PASSWORD_MISMATCH);
    }

    // 3. 새 비밀번호가 기존 비밀번호와 동일한지 확인 (재사용 방지)
    if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
      throw new ApiException(MemberErrorCode.PASSWORD_SAME_AS_OLD);
    }

    // 4. 암호화 및 변경
    String encodedPassword = passwordEncoder.encode(request.getNewPassword());
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
  public void withdraw(Long memberId, String accessToken, String refreshToken) {
    Member member = getMemberOrThrow(memberId);

    if (member.getStatus() == MemberStatus.QUIT) {
      throw new ApiException(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
      tokenBlacklistService.addToBlacklist(accessToken);
    }

    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
      refreshTokenRepository.deleteByToken(refreshToken);
    }

    member.withdraw();
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