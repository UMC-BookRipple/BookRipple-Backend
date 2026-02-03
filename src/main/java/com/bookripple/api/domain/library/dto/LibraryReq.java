package com.bookripple.api.domain.library.dto;

import java.util.List;

import com.bookripple.api.domain.library.enums.LibraryStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LibraryReq {

    @Getter
    @NoArgsConstructor
    public static class Delete {

        private LibraryStatus category;

        @NotEmpty(message = "항목을 최소 1개 이상 선택해야 합니다.")
        private List<Long> bookIds;
    }
}
