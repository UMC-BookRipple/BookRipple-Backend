package com.bookripple.api.global.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class GlobalDto {

  @Builder
  public record IdRes(
      Long id
  ) {

  }


  public record ContentReq(
      @NotBlank
      String content
  ) {

  }
}
