package com.bookripple.api.domain.reading.service;

import com.bookripple.api.domain.reading.dto.ReadingDto;

public interface ReadingService {
    ReadingDto.StartRes start(Long memberId, ReadingDto.StartReq req);

    ReadingDto.PauseRes pause(Long memberId, Long sessionId);

    ReadingDto.EndRes end(Long memberId, ReadingDto.EndReq req);

    ReadingDto.CompleteRes complete(Long memberId, ReadingDto.CompleteReq req);

    /**
     * 주별 독서 그래프 조회 (최근 7일)
     */
    ReadingDto.WeeklyReadingGraphRes getWeeklyReadingGraph(Long memberId);
}
