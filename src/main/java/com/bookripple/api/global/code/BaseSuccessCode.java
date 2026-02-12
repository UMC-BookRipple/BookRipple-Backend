package com.bookripple.api.global.code;

import org.springframework.http.HttpStatus;

public interface BaseSuccessCode {

  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}
