package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 회원가입
   */
  @Transactional
  public GlobalDto.IdRes signup(AuthReqDto.Signup request) {

    // 로그인 아이디 중복 검증
    validateDuplicateLoginId(request.getLoginId());

    // TODO: 이메일 인증 완료 여부 검증 (SIGN_UP 목적)
    // emailVerificationService.validateVerified(request.getEmail(), SIGN_UP);

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    Member member = Member.builder()
        .loginId(request.getLoginId())
        .password(encodedPassword)
        .name(request.getName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .isRequiredAgreed(request.getIsRequiredAgreed())
        .isOptionalAgreed(request.getIsOptionalAgreed())
        .isCertified(true) // 가입 완료 시점에서는 인증된 상태로 간주
        .loginType(LoginType.LOCAL)
        .build();

    Member savedMember = memberRepository.save(member);

    return new GlobalDto.IdRes(savedMember.getId());
  }

  /**
   * 로컬 로그인
   */
  @Transactional(readOnly = true)
  public AuthResDto.Login localLogin(AuthReqDto.Login request) {

    Member member = memberRepository.findByLoginId(request.getLoginId())
        .orElseThrow(() -> new ApiException(
            CommonErrorCode.NOT_FOUND,
            "존재하지 않는 로그인 아이디입니다."
        ));

    validateLocalMember(member);
    validatePassword(request.getPassword(), member.getPassword());

    // TODO: 이메일 인증 여부 로그인 정책 결정
    // if (!member.getIsCertified()) { ... }

    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), "USER");

    return AuthResDto.Login.builder()
        .memberId(member.getId())
        .accessToken(accessToken)
        .build();
  }

  /**
   * 아이디 중복 여부 조회 (Controller 용)
   */
  @Transactional(readOnly = true)
  public boolean isLoginIdAvailable(String loginId) {
    return !memberRepository.existsByLoginId(loginId);
  }

  /**
   * 아이디 중복 검증 (Service 내부용)
   */
  private void validateDuplicateLoginId(String loginId) {
    if (memberRepository.existsByLoginId(loginId)) {
      throw new ApiException(
          CommonErrorCode.BAD_REQUEST,
          "이미 사용 중인 로그인 아이디입니다."
      );
    }
  }

  private void validateLocalMember(Member member) {
    if (member.getLoginType() != LoginType.LOCAL) {
      throw new ApiException(
          CommonErrorCode.BAD_REQUEST,
          "로컬 로그인 계정이 아닙니다."
      );
    }
  }

  private void validatePassword(String rawPassword, String encodedPassword) {
    if (encodedPassword == null || !passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new ApiException(
          CommonErrorCode.UNAUTHORIZED,
          "비밀번호가 올바르지 않습니다."
      );
    }
  }
}
