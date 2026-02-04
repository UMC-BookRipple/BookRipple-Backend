package com.bookripple.api.domain.library.dto;

import lombok.Builder;
import lombok.Getter;

public class LibraryRes {

    @Getter
    @Builder
    public static class Delete {
        private long deletedCount;

        public static Delete of(long deletedCount) {
            return Delete.builder()
                    .deletedCount(deletedCount)
                    .build();
        }
    }
}
