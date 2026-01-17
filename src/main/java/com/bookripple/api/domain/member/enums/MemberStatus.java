package com.bookripple.api.domain.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
    ACTIVE("활성"),
    SLEEP("휴면"),
    QUIT("탈퇴");

    private final String description;
}