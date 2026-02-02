package com.bookripple.api.domain.reading.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.reading.dto.ReadingDto;
import com.bookripple.api.domain.reading.service.ReadingService;
import com.bookripple.api.global.annotation.PreventDuplicate;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/reading")
public class ReadingController {

    private final ReadingService readingService;

    // 독서 시작
    @PreventDuplicate
    @PostMapping("/start")
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
    @PreventDuplicate
    @PostMapping("/{session-id}/pause")
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