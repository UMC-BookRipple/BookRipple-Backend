package com.bookripple.api.domain.reading.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.reading.dto.ReadingDto;
import com.bookripple.api.domain.reading.service.ReadingService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Reading",
        description = "독서 세션 관련 API"
)
@RequestMapping("/api/v1/reading")
public class ReadingController {

    private final ReadingService readingService;

    // 독서 시작
    @PreventDuplicate
    @PostMapping("/start")
    @Operation(summary = "독서 시작", description = "사용자가 특정 도서에 대한 독서를 시작합니다.")
    public ApiResponse<ReadingDto.StartRes> start(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReadingDto.StartReq request
    ) {
        return ApiResponse.onSuccess(
                CommonSuccessCode.CREATED,
                readingService.start(memberId, request)
        );
    }

    // 독서 일시정지
    @Tag(
            name = "Reading",
            description = "독서 세션 관련 API"
    )
    @PreventDuplicate
    @PostMapping("/{session-id}/pause")
    @Operation(summary = "독서 일시정지", description = "사용자가 현재 진행 중인 독서 세션을 일시정지합니다.")
    public ApiResponse<ReadingDto.PauseRes> pause(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("session-id") @Min(1) Long sessionId
    ) {
        return ApiResponse.onSuccess(
                CommonSuccessCode.OK,
                readingService.pause(memberId, sessionId)
        );
    }

    // 독서 종료
    @PreventDuplicate
    @PostMapping("/end")
    @Operation(
            summary = "독서 종료",
            description = "사용자가 현재 진행 중인 독서 세션을 종료합니다."
    )
    public ApiResponse<ReadingDto.EndRes> end(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReadingDto.EndReq request
    ) {
        return ApiResponse.onSuccess(
                CommonSuccessCode.CREATED,
                readingService.end(memberId, request)
        );
    }

    // 완독 설정
    @PreventDuplicate
    @PostMapping("/complete")
    @Operation(
            summary = "완독 설정",
            description = "사용자가 특정 도서에 대해 완독 상태로 설정합니다."
    )
    public ApiResponse<ReadingDto.CompleteRes> complete(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReadingDto.CompleteReq request
    ) {
        return ApiResponse.onSuccess(
                CommonSuccessCode.OK,
                readingService.complete(memberId, request)
        );
    }
}