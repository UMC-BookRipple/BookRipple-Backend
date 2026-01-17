package com.bookripple.api.domain.auth.service;

import com.bookripple.api.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  private final JwtTokenProvider jwtTokenProvider;

  @Transactional(readOnly = true)
  public AuthResDto.Login localLogin(AuthReqDto.Login request) {

    // TODO: DB 연동 전 임시 mock member (제거 예정)
    Member member = getMockMember(request.getLoginId());

    /*
    Member member =
        memberRepository
            .findByLoginId(request.getLoginId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로그인 아이디입니다."));
    */

    validateLocalMember(member);
    validatePassword(request.getPassword(), member.getPassword());

    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), "USER");

    return AuthResDto.Login.builder()
        .memberId(member.getId())
        .accessToken(accessToken)
        .build();
  }

  // TODO: DB 연동 전 임시 mock member (제거 예정)
  private Member getMockMember(String loginId) {
    return Member.builder()
        .id(1L)
        .loginId(loginId)
        .password(passwordEncoder.encode("1234"))
        .loginType(LoginType.LOCAL)
        .build();
  }

  private void validateLocalMember(Member member) {
    if (member.getLoginType() != LoginType.LOCAL) {
      throw new IllegalArgumentException("로컬 로그인 계정이 아닙니다.");
    }
  }

  private void validatePassword(String rawPassword, String encodedPassword) {
    if (encodedPassword == null
        || !passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
    }
  }
}
