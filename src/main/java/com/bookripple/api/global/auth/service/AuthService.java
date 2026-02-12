package com.bookripple.api.global.auth.service;

import com.bookripple.api.domain.member.code.MemberErrorCode;
import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;
import com.bookripple.api.domain.member.enums.MemberRole;
import com.bookripple.api.domain.member.repository.MemberRepository;
import com.bookripple.api.domain.verification.email.enums.EmailVerificationPurpose;
import com.bookripple.api.domain.verification.email.service.EmailVerificationService;
import com.bookripple.api.global.auth.code.AuthErrorCode;
import com.bookripple.api.global.auth.dto.AuthReqDto;
import com.bookripple.api.global.auth.dto.AuthResDto;
import com.bookripple.api.global.auth.entity.RefreshToken;
import com.bookripple.api.global.auth.repository.RefreshTokenRepository;
import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.dto.GlobalDto;
import com.bookripple.api.global.error.ApiException;
import com.bookripple.api.global.security.JwtTokenProvider;
import com.bookripple.api.global.service.TokenBlacklistService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
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
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailVerificationService emailVerificationService;
  private final RestClient restClient;
  private final TokenBlacklistService tokenBlacklistService;

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
  @Transactional
  public AuthResDto.Login localLogin(AuthReqDto.Login request) {

    Member member = memberRepository.findByLoginId(request.getLoginId())
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    validateLocalMember(member);
    validatePassword(request.getPassword(), member.getPassword());

    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), "USER");
    String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

    // 기존 RefreshToken 삭제 후 새로 저장
    refreshTokenRepository.deleteByMemberId(member.getId());
    saveRefreshToken(member.getId(), refreshToken);

    return AuthResDto.Login.builder()
        .memberId(member.getId())
        .userName(member.getName())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
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
          String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

          // 기존 RefreshToken 삭제 후 새로 저장
          refreshTokenRepository.deleteByMemberId(member.getId());
          saveRefreshToken(member.getId(), refreshToken);

          return AuthResDto.Login.builder()
              .memberId(member.getId())
              .userName(member.getName())
              .accessToken(accessToken)
              .refreshToken(refreshToken)
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
          String refreshToken = jwtTokenProvider.createRefreshToken(savedMember.getId());

          saveRefreshToken(savedMember.getId(), refreshToken);

          return AuthResDto.Login.builder()
              .memberId(savedMember.getId())
              .userName(savedMember.getName())
              .accessToken(accessToken)
              .refreshToken(refreshToken)
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

  /**
   * 게스트 로그인
   */
  @Transactional
  public AuthResDto.Login guestLogin() {
    String guestIdentifier = UUID.randomUUID().toString().substring(0, 8);
    String guestLoginId = "guest_" + guestIdentifier;

    Member guestMember = Member.builder()
        .loginId(guestLoginId)
        .password(passwordEncoder.encode("GUEST_PASSWORD"))
        .name("게스트" + guestIdentifier)
        .email(guestLoginId + "@guest.com")
        .loginType(LoginType.GUEST)
        .role(MemberRole.USER)
        .isRequiredAgreed(true)
        .isOptionalAgreed(false)
        .isCertified(true)
        .build();

    memberRepository.save(guestMember);

    String accessToken = jwtTokenProvider.createAccessToken(guestMember.getId(), "USER");
    String refreshToken = jwtTokenProvider.createRefreshToken(guestMember.getId());

    saveRefreshToken(guestMember.getId(), refreshToken);

    return AuthResDto.Login.builder()
        .memberId(guestMember.getId())
        .userName(guestMember.getName())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .isNewMember(true)
        .build();
  }

  /**
   * 로그아웃
   */
  @Transactional
  public void logout(String accessToken, String refreshToken) {
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new ApiException(AuthErrorCode.INVALID_TOKEN);
    }

    // AccessToken 블랙리스트 추가
    tokenBlacklistService.addToBlacklist(accessToken);

    // RefreshToken 삭제
    if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
      refreshTokenRepository.deleteByToken(refreshToken);
    }
  }

  /**
   * RefreshToken으로 AccessToken 재발급
   */
  @Transactional
  public AuthResDto.TokenRefresh refreshAccessToken(String refreshTokenValue) {
    // RefreshToken 유효성 검증
    if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
      throw new ApiException(AuthErrorCode.INVALID_TOKEN);
    }

    // RefreshToken이 "refresh" 타입인지 확인
    String tokenType = jwtTokenProvider.getTokenType(refreshTokenValue);
    if (!"refresh".equals(tokenType)) {
      throw new ApiException(AuthErrorCode.INVALID_TOKEN);
    }

    // DB에 저장된 RefreshToken 확인
    RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
        .orElseThrow(() -> new ApiException(AuthErrorCode.INVALID_TOKEN));

    // RefreshToken 만료 여부 확인
    if (refreshToken.isExpired()) {
      refreshTokenRepository.delete(refreshToken);
      throw new ApiException(AuthErrorCode.EXPIRED_TOKEN);
    }

    Long memberId = refreshToken.getMemberId();

    // 회원 존재 여부 확인
    memberRepository.findById(memberId)
        .orElseThrow(() -> new ApiException(MemberErrorCode.MEMBER_NOT_FOUND));

    // 새로운 AccessToken 및 RefreshToken 발급
    String newAccessToken = jwtTokenProvider.createAccessToken(memberId, "USER");
    String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

    // 기존 RefreshToken 삭제 후 새로 저장
    refreshTokenRepository.delete(refreshToken);
    saveRefreshToken(memberId, newRefreshToken);

    return AuthResDto.TokenRefresh.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  /**
   * RefreshToken 저장
   */
  private void saveRefreshToken(Long memberId, String token) {
    LocalDateTime expiresAt = jwtTokenProvider.getExpirationDate(token)
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();

    RefreshToken refreshToken = RefreshToken.builder()
        .memberId(memberId)
        .token(token)
        .expiresAt(expiresAt)
        .build();

    refreshTokenRepository.save(refreshToken);
  }
}