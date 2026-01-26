package com.bookripple.api.domain.auth.service;

import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.enums.MemberRole;
import com.bookripple.api.domain.member.enums.MemberStatus;
import com.bookripple.api.domain.member.exception.DuplicateEmailException;
import com.bookripple.api.domain.member.exception.DuplicateLoginIdException;
import com.bookripple.api.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Long signup(AuthReqDto.Signup request) {

    if (memberRepository.existsByLoginId(request.getLoginId())) {
      throw new DuplicateLoginIdException();
    }

    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateEmailException();
    }

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    Member member = Member.builder()
        .loginId(request.getLoginId())
        .password(encodedPassword)
        .name(request.getName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .isRequiredAgreed(request.getIsRequiredAgreed())
        .isOptionalAgreed(request.getIsOptionalAgreed()) // 필드명 변경 반영
        .isCertified(false) // 기본값: 이메일 미인증
        .status(MemberStatus.ACTIVE) // 기본 상태
        .loginType(LoginType.LOCAL)
        .role(MemberRole.USER)
        .build();

    return memberRepository.save(member).getId();
  }
}
