package com.bookripple.api.global.security;

import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

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
