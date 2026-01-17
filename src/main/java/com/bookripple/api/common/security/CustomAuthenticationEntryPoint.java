package com.bookripple.api.common.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.bookripple.api.common.code.CommonErrorCode;
import com.bookripple.api.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    response.setStatus(CommonErrorCode.UNAUTHORIZED.getHttpStatus().value());
    response.setContentType("application/json;charset=UTF-8");

    ApiResponse<?> body = ApiResponse.onFailure(CommonErrorCode.UNAUTHORIZED, null);
    objectMapper.writeValue(response.getWriter(), body);
  }
}
