package com.bookripple.api.global.converter;

import com.bookripple.api.global.dto.GlobalDto.IdRes;

public class GlobalConverter {

  public static IdRes toIdRes(Long id) {
    return IdRes.builder()
        .id(id)
        .build();
  }
}
