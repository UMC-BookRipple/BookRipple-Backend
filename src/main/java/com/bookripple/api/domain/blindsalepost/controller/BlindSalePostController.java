package com.bookripple.api.domain.blindsalepost.controller;

import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.service.BlindSalePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blind-books") // API 리스트에 정의된 공통 경로
@RequiredArgsConstructor
public class BlindSalePostController {

    // 인터페이스 타입으로 주입받아 유연성을 높입니다
    private final BlindSalePostService blindSalePostService;

    @PostMapping // 판매 도서 등록(게시글 만들기)
    public ResponseEntity<BlindSalePostResDto.Create> create(
            @RequestBody BlindSalePostReqDto.Create requestDto) {

        /* * 실제 운영 환경에서는 Spring Security 등을 통해
         * 로그인한 유저의 ID를 가져오겠지만,
         * 현재는 구현 흐름을 잡기 위해 임시 ID(1L)를 사용합니다.
         */
        Long loginMemberId = 1L;

        // 서비스를 호출하여 비즈니스 로직을 실행하고 결과를 받습니다
        BlindSalePostResDto.Create response = blindSalePostService.createPost(loginMemberId, requestDto);

        // 생성 성공 시 201 Created 상태 코드와 함께 응답 DTO를 반환합니다
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}