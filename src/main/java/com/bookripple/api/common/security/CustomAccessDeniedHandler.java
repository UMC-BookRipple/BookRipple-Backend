package com.bookripple.api.common.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    response.setStatus(CommonErrorCode.FORBIDDEN.getHttpStatus().value());
    response.setContentType("application/json;charset=UTF-8");

    ApiResponse<?> body = ApiResponse.onFailure(CommonErrorCode.FORBIDDEN, null);
    objectMapper.writeValue(response.getWriter(), body);
  }
}
