package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;

public interface BlindSalePostService {
    // 판매 도서 등록 (게시글 만들기)
    BlindSalePostResDto.Create createPost(Long memberId, BlindSalePostReqDto.Create dto);

    // 판매 게시글 상세 조회
    public BlindSalePostResDto.Detail getPostDetail(Long blindPostId);
}
