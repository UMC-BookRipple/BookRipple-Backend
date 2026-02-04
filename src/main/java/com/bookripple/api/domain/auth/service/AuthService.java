package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.AuthErrorCode;
import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.code.MemberErrorCode;
import com.bookripple.api.common.error.ApiException;
import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.verification.email.service.EmailVerificationService;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.security.JwtTokenProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailVerificationService emailVerificationService;
  private final RestClient restClient;

  @Value("${kakao.client-id}")
  private String kakaoClientId;

  @Value("${kakao.client-secret}")
  private String kakaoClientSecret;

  @Value("${kakao.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${kakao.token-uri}")
  private String kakaoTokenUri;

  @Value("${kakao.user-info-uri}")
  private String kakaoUserInfoUri;


  /**
   * 회원가입
   */
  @Transactional
  public GlobalDto.IdRes signup(AuthReqDto.Signup request) {

    validateDuplicateLoginId(request.getLoginId());

    emailVerificationService.validateVerified(
        request.getEmail(),
        EmailVerificationPurpose.SIGN_UP
    );

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    Member member = Member.builder()
        .loginId(request.getLoginId())
        .password(encodedPassword)
        .name(request.getName())
        .email(request.getEmail())
        .birthDate(request.getBirthDate())
        .isRequiredAgreed(request.getIsRequiredAgreed())
        .isOptionalAgreed(request.getIsOptionalAgreed())
        .isCertified(true)
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
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    validateLocalMember(member);
    validatePassword(request.getPassword(), member.getPassword());

    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), "USER");

    return AuthResDto.Login.builder()
        .memberId(member.getId())
        .userName(member.getName())
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
      throw new ApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);
    }
  }

  /**
   * 카카오 로그인
   */
  @Transactional
  public AuthResDto.Login kakaoLogin(String code) {
    String kakaoAccessToken = requestKakaoAccessToken(code);
    Map<String, Object> kakaoUser = requestKakaoUserInfo(kakaoAccessToken);

    String providerId = String.valueOf(kakaoUser.get("id"));
    Map<String, Object> properties = (Map<String, Object>) kakaoUser.get("properties");
    String nickname = (String) properties.get("nickname");

    return memberRepository.findByProviderId(providerId)
        .map(member -> {
          String accessToken = jwtTokenProvider.createAccessToken(member.getId(), "USER");
          return AuthResDto.Login.builder()
              .memberId(member.getId())
              .userName(member.getName())
              .accessToken(accessToken)
              .isNewMember(false)
              .build();
        })
        .orElseGet(() -> {
          Member newMember = Member.builder()
              .loginId("kakao_" + providerId)
              .providerId(providerId)
              .name(nickname)
              .loginType(LoginType.KAKAO)
              .isRequiredAgreed(true)
              .requiredAgreedAt(java.time.LocalDateTime.now())
              .isOptionalAgreed(false)
              .isCertified(true)
              .build();

          Member savedMember = memberRepository.save(newMember);
          String accessToken = jwtTokenProvider.createAccessToken(savedMember.getId(), "USER");

          return AuthResDto.Login.builder()
              .memberId(savedMember.getId())
              .userName(savedMember.getName())
              .accessToken(accessToken)
              .isNewMember(true)
              .build();
        });
  }

  private void validateLocalMember(Member member) {
    if (member.getLoginType() != LoginType.LOCAL) {
      throw new ApiException(MemberErrorCode.SOCIAL_PROFILE_RESTRICTION);
    }
  }

  private void validatePassword(String rawPassword, String encodedPassword) {
    if (encodedPassword == null || !passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new ApiException(AuthErrorCode.LOGIN_FAILED);
    }
  }

  private String requestKakaoAccessToken(String code) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("client_secret", kakaoClientSecret);
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("code", code);

    Map<String, Object> response = restClient.post()
        .uri(kakaoTokenUri)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(params)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
          throw new ApiException(CommonErrorCode.BAD_GATEWAY, "카카오 토큰 발급 실패");
        })
        .body(Map.class);

    if (response == null || !response.containsKey("access_token")) {
      throw new ApiException(CommonErrorCode.BAD_GATEWAY, "카카오 토큰 응답이 비어있습니다.");
    }

    return (String) response.get("access_token");
  }

  private Map<String, Object> requestKakaoUserInfo(String kakaoAccessToken) {
    return restClient.get()
        .uri(kakaoUserInfoUri)
        .headers(headers -> headers.setBearerAuth(kakaoAccessToken))
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
          throw new ApiException(CommonErrorCode.BAD_GATEWAY, "카카오 사용자 정보 조회 실패");
        })
        .body(Map.class);
  }

  /**
   * 아이디 찾기
   */
  @Transactional(readOnly = true)
  public String findLoginIdByEmail(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    validateLocalMember(member);

    return member.getLoginId();
  }

  /**
   * 비밀번호 재설정
   */
  @Transactional
  public void resetPassword(AuthReqDto.PasswordReset request) {
    emailVerificationService.validateVerified(
        request.getEmail(),
        EmailVerificationPurpose.FIND_PW
    );

    Member member = memberRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    validateLocalMember(member);

    String encodedPassword = passwordEncoder.encode(request.getNewPassword());
    member.updatePassword(encodedPassword);
  }
}