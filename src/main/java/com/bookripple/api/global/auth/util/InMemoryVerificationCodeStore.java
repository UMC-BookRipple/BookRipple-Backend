package com.bookripple.api.global.auth.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryVerificationCodeStore implements VerificationCodeStore {

  private final Map<String, String> store = new ConcurrentHashMap<>();

  @Override
  public void save(String email, String code) {
    store.put(email, code);
  }

  @Override
  public Optional<String> get(String email) {
    return Optional.ofNullable(store.get(email));
  }

  @Override
  public void remove(String email) {
    store.remove(email);
  }
}