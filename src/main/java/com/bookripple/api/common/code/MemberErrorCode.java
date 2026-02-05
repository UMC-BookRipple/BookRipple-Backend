package com.bookripple.api.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

  // 400 BAD_REQUEST: 회원 로직상 불가능한 요청
  PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "MEMBER_400_1", "새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다."),
  SOCIAL_PROFILE_RESTRICTION(HttpStatus.BAD_REQUEST, "MEMBER_400_2", "간편 로그인(카카오 등) 회원은 수정할 수 없는 정보입니다."),
  EMAIL_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "MEMBER_400_3", "기존 이메일과 동일한 이메일로 변경할 수 없습니다."),
  PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER_400_4", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),

  // 401 UNAUTHORIZED: 본인 확인 실패 (마이페이지 진입 시 등)
  PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "MEMBER_401_1", "현재 비밀번호가 일치하지 않습니다."),

  // 404 NOT_FOUND: 리소스 없음
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_404_1", "존재하지 않는 회원입니다."),

  // 409 CONFLICT: 중복 데이터
  DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER_409_1", "이미 사용 중인 아이디입니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_409_2", "이미 사용 중인 이메일입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}