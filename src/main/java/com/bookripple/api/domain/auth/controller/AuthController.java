package com.bookripple.api.domain.auth.controller;

import com.bookripple.api.common.code.CommonSuccessCode;
import com.bookripple.api.common.response.ApiResponse;
import com.bookripple.api.domain.auth.dto.AuthResDto.Login;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookripple.api.domain.auth.dto.AuthReqDto;
import com.bookripple.api.domain.auth.dto.AuthResDto;
import com.bookripple.api.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login/local")
  public ResponseEntity<ApiResponse<Login>> login(
      @RequestBody @Valid AuthReqDto.Login request) {

    AuthResDto.Login result = authService.localLogin(request);

    return ResponseEntity
        .status(CommonSuccessCode.OK.getHttpStatus())
        .body(ApiResponse.onSuccess(CommonSuccessCode.OK, result));
  }

}
