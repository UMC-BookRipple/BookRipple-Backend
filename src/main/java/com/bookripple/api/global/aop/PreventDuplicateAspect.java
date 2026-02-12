package com.bookripple.api.global.aop;

import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.error.ApiException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PreventDuplicateAspect {

  private final Cache<String, Object> cache = Caffeine.newBuilder()
      .expireAfterWrite(3, TimeUnit.SECONDS)
      .maximumSize(1000)
      .build();

  @Around("@annotation(com.bookripple.api.global.annotation.PreventDuplicate)")
  public Object checkDuplicate(ProceedingJoinPoint joinPoint)
      throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();

    String memberId = getMemberId();

    String key = generateKey(request, memberId);

    Object existingValue = cache.asMap().putIfAbsent(key, true);

    if (existingValue != null) {
      throw new ApiException(CommonErrorCode.TOO_MANY_REQUESTS);
    }

    return joinPoint.proceed();
  }

  private String getMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return String.valueOf(authentication.getPrincipal());
  }

  private String generateKey(HttpServletRequest request, String memberId) {
    String url = request.getRequestURI();
    String method = request.getMethod();

    return String.format("%s:%s:%s", memberId, method, url);
  }
}
