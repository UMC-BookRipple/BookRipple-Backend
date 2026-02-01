package com.bookripple.api.domain.auth.service;

import com.bookripple.api.common.code.CommonErrorCode;
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

    // 🔥 이메일 인증 완료 여부 검증 (회원가입 목적)
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
        .isCertified(true) // 가입 이후 상태
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
      throw new ApiException(
          CommonErrorCode.CONFLICT,
          "이미 사용 중인 로그인 아이디입니다."
      );
    }
  }

  /**
   * kakao 로그인
   */
  public AuthResDto.Login kakaoLogin(String code) {

    // 1️⃣ 인가 코드 → 카카오 access token
    String kakaoAccessToken = requestKakaoAccessToken(code);

    // 2️⃣ access token → 카카오 사용자 정보
    Map<String, Object> kakaoUser = requestKakaoUserInfo(kakaoAccessToken);

    // 3️⃣ providerId 추출
    Long providerId = ((Number) kakaoUser.get("id")).longValue();

    // 4️⃣ nickname 추출
    Map<String, Object> properties =
        (Map<String, Object>) kakaoUser.get("properties");

    String nickname = (String) properties.get("nickname");

    // 🔴 아직은 반환에 쓰지 않음 (다음 단계)
    return AuthResDto.Login.builder()
        .memberId(0L)
        .userName(nickname)
        .accessToken("KAKAO_DUMMY_TOKEN")
        .isNewMember(false)
        .build();
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
}
