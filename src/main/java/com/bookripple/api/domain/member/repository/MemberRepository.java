package com.bookripple.api.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookripple.api.domain.member.entity.Member;
import com.bookripple.api.domain.member.enums.LoginType;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  // 로그인 (LOCAL)
  Optional<Member> findByLoginId(String loginId);

  // 아이디 중복 체크
  boolean existsByLoginId(String loginId);

  // 이메일 중복 체크
  boolean existsByEmail(String email);

  // 이메일 조회 (중복 체크 / OAuth 대비)
  Optional<Member> findByEmail(String email);

  // 소셜 로그인 대비 (카카오 등)
  Optional<Member> findByEmailAndLoginType(String email, LoginType loginType);

}
