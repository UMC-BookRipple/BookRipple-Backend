package com.bookripple.api.domain.library.dto;

import java.util.List;

import com.bookripple.api.domain.library.enums.LibraryStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LibraryDto {

    @Getter
    @NoArgsConstructor
    public static class DeleteReq {
        @NotEmpty(message = "항목을 최소 1개 이상 선택해야 합니다.")
        private List<Long> bookIds;
    }

    @Getter
    @Builder
    public static class DeleteRes {
        private long deletedCount;

        public static LibraryDto.DeleteRes of(long deletedCount) {
            return LibraryDto.DeleteRes.builder()
                    .deletedCount(deletedCount)
                    .build();
        }
    }
}
