package com.bookripple.api.domain.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class ReviewMemoResDto {

  @Builder
  public record ReviewAndMemo(
      // 감상평
      String bookTitle,
      String reviewWriter, // 리뷰 작성자
      String reviewContent,
      LocalDateTime reviewUpdatedAt,
      // 내가 작성한 메모
      Long reviewMemoId,
      String memoContent, // 리뷰 메모 내용
      LocalDateTime memoUpdatedAt
  ) {

  }

  @Builder
  public record MyReviewMemoList(
      List<ReviewAndMemo> memoList,
      Boolean hasNext,
      String lastBookTitle,
      Long lastId
  ) {

  }

}
