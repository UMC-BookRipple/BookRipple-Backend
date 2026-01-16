package com.bookripple.api.domain.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginType {
    LOCAL("일반 로그인"),
    KAKAO("카카오 로그인"),
    GUEST("게스트");

    private final String description;
}