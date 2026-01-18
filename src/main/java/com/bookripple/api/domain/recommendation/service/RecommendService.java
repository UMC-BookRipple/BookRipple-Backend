package com.bookripple.api.domain.recommendation.service;

import com.bookripple.api.domain.recommendation.dto.RecommendReqDto.Create;
import com.bookripple.api.global.dto.GlobalDto.IdRes;

public interface RecommendService {

  IdRes createRecommendation(Long memberId, Long sourceBookId, Create request);
}
