package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.util.EmailSender;
import com.bookripple.api.domain.auth.util.VerificationCodeStore;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

  private final EmailSender emailSender;
  private final VerificationCodeStore codeStore;
  private final MemberRepository memberRepository;

  // 인증코드 생성 및 이메일 발송
  public void sendVerificationCode(String email) {
    String code = generateCode();
    emailSender.send(email, "BookRipple 인증코드", "인증코드: " + code);
    codeStore.save(email, code);
  }

  // 인증코드 검증 및 이메일 인증 처리
  @Transactional
  public void verifyCode(String email, String inputCode) {
    String savedCode = codeStore.get(email)
        .orElseThrow(() -> new ApiException(CommonErrorCode.NOT_FOUND, "인증코드를 찾을 수 없습니다."));

    if (!savedCode.equals(inputCode)) {
      throw new ApiException(CommonErrorCode.BAD_REQUEST, "인증코드가 일치하지 않습니다.");
    }

    // 인증 처리 (isCertified = true)
    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    optionalMember.ifPresent(Member::certify);

    // 인증 성공 시 저장된 인증코드 제거
    codeStore.remove(email);
  }

  private String generateCode() {
    return String.format("%06d", new Random().nextInt(1_000_000));
  }
}
