package com.bookripple.api.global.security;

import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

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
