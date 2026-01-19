package com.bookripple.api.global.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class GlobalDto {

  @Builder
  public record IdRes(
      Long id
  ) {

  }

  @Builder
  public record SingleRes<T>(
      T data
  ) {

  }


  public record ContentReq(
      @NotBlank
      String content
  ) {

  }
}
