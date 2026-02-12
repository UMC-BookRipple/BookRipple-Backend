package com.bookripple.api.global.auth.repository;

import com.bookripple.api.global.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByMemberId(Long memberId);

  void deleteByMemberId(Long memberId);

  void deleteByToken(String token);
}
