package com.bookripple.api.global.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final long accessTokenExpirationMs;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
  }

  public String createAccessToken(Long memberId, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

    return Jwts.builder()
        .subject(String.valueOf(memberId))
        .claim("role", role)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Long getMemberId(String token) {
    Claims claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return Long.parseLong(claims.getSubject());
  }

  public long getRemainingTime(String token) {
    Date expiration = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();

    long now = new Date().getTime();
    return expiration.getTime() - now;
  }
}
