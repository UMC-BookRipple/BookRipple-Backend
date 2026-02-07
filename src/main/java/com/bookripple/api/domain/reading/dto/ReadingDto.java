package com.bookripple.api.domain.reading.dto;

import java.math.BigDecimal;
import lombok.*;

public class ReadingDto {

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class StartReq {
        private Long bookId;
    }

    @Getter @Builder
    public static class StartRes {
        private Long sessionId;
    }

    @Getter @Builder
    public static class PauseRes {
        private Long sessionId;
        private int accumulatedTime; // seconds
        private String status;       // PAUSED
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class EndReq {
        private Long sessionId;
        private int pagesReadStart; // 이번 세션 시작 시점 페이지
        private int pagesReadEnd; // 이번 세션 종료 시점 페이지
    }

    @Getter @Builder
    public static class EndRes {
        private Long recordId;
        private int readingTime;      // 이번 세션 seconds
        private int totalReadingTime; // 누적 seconds
        private BigDecimal progress; // 계산 로직 필요
        private boolean isCompleted;
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class CompleteReq {
        private Long bookId;
    }

    @Getter @Builder
    public static class CompleteRes {
        private Long bookId;
        private BigDecimal progress;
        private boolean isCompleted;
    }
}
