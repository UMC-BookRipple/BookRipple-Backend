package com.bookripple.api.domain.blindsalepost.service;

import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostReqDto;
import com.bookripple.api.domain.blindsalepost.dto.BlindSalePostResDto;
import com.bookripple.api.domain.blindsalepost.enums.PostStatus;

public interface BlindSalePostService {
    // 판매 도서 등록 (게시글 만들기)
    BlindSalePostResDto.Create createPost(Long memberId, BlindSalePostReqDto.Create dto);

    // 판매 게시글 상세 조회
    BlindSalePostResDto.Detail getPostDetail(Long blindPostId);

    BlindSalePostResDto.SliceResponse getMyPostList(Long memberId, PostStatus status, Long cursor, int size);

    void updatePost(Long memberId, Long blindBookId, BlindSalePostReqDto.Update request);

    void deletePost(Long memberId, Long blindBookId);

    BlindSalePostResDto.PurchaseRequestList getPurchaseRequests(Long blindPostId);

    BlindSalePostResDto.BuyerSliceResponse<BlindSalePostResDto.BuyerListElement> getAllPosts(Long cursor, int size);

    BlindSalePostResDto.BuyerSliceResponse<BlindSalePostResDto.MyRequestListElement> getMyRequests(Long memberId, Long cursor, int size);

    BlindSalePostResDto.BuyerDetail getPostDetailForBuyer(Long memberId, Long blindPostId);
}
