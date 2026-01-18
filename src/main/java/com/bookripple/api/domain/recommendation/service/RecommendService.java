package com.bookripple.api.domain.recommendation.service;

import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.global.dto.GlobalDto.ContentReq;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface RecommendService {

  IdRes createRecommendation(Long memberId, Long sourceBookId, Create request);

  IdRes updateRecommendation(Long memberId, Long recommendationId, ContentReq request);

  IdRes deleteRecommendation(Long memberId, Long recommendationId);
}
