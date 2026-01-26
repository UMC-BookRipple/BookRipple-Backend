package com.bookripple.api.domain.auth.util;

import java.util.Optional;

public interface VerificationCodeStore {

  void save(String email, String code);

  Optional<String> get(String email);

  void remove(String email);
}
