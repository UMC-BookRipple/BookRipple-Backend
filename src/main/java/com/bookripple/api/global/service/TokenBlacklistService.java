package com.bookripple.api.global.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

  private final Cache<String, String> blacklist;

  public TokenBlacklistService(@Value("${jwt.access-token-expiration-ms}") long jwtExpirationMs) {
    this.blacklist = Caffeine.newBuilder()
        .expireAfterWrite(jwtExpirationMs, TimeUnit.MILLISECONDS)
        .maximumSize(10_000)
        .build();
  }

  // 블랙리스트 추가
  public void addToBlacklist(String token) {
    blacklist.put(token, "logout");
  }

  // 블랙리스트 확인
  public boolean isBlacklisted(String token) {
    return blacklist.getIfPresent(token) != null;
  }
}