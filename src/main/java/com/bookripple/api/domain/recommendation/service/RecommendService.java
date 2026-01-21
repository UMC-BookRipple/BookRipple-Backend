package com.bookripple.api.domain.recommendation.service;

import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.MyRecommendList;
import com.bookripple.api.domain.recommendation.dto.RecommendResDto.RecommendList;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface RecommendService {

  IdRes createRecommendation(Long memberId, Long sourceBookId, Create request);

  IdRes updateRecommendation(Long memberId, Long recommendationId, ContentReq request);

  IdRes deleteRecommendation(Long memberId, Long recommendationId);

  RecommendList getRecommendList(Long memberId, Long bookId, Long lastId, int size);

  MyRecommendList getMyRecommendList(Long memberId, String lastBookTitle, Long lastId, int size);
}
