package com.bookripple.api.global.error;


import com.bookripple.api.global.code.BaseErrorCode;
import com.bookripple.api.global.code.CommonErrorCode;
import com.bookripple.api.global.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 에러 처리 핸들러
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<?>> handleApiException(ApiException exception) {
    BaseErrorCode errorCode = exception.getErrorCode();
    String overrideMessage = resolveOverrideMessage(exception, errorCode);
    return buildResponse(errorCode, overrideMessage, exception.getFieldErrors());
  }

  // @Valid DTO 검증 실패 처리, 여러 예외 발생 시 Map으로 모아서 처리 가능
  // 예) @Notblank, @Email 등
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception) {
    Map<String, String> fieldErrors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            this::resolveFieldErrorMessage,
            this::mergeMessages
        ));
    return buildResponse(CommonErrorCode.BAD_REQUEST, null, fieldErrors);
  }

  // @Validated 파라미터 검증 실패 처리
  // 예) @RequestParam @Min(1) int page 등
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<?>> handleConstraintViolation(
      ConstraintViolationException exception) {
    Map<String, String> fieldErrors = exception.getConstraintViolations()
        .stream()
        .collect(Collectors.toMap(
            this::resolveConstraintField,
            ConstraintViolation::getMessage,
            this::mergeMessages
        ));
    return buildResponse(CommonErrorCode.BAD_REQUEST, null, fieldErrors);
  }

  // json 요청 깨짐 또는 타입 오류, enum 값 오류 등
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception) {
    String overrideMessage = exception.getMessage();
    return buildResponse(CommonErrorCode.BAD_REQUEST, overrideMessage, null);
  }

  // 필수 RequestParam 누락 시 에러 처리 (query 등)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameter(
      MissingServletRequestParameterException exception
  ) {
    return buildResponse(CommonErrorCode.BAD_REQUEST, exception.getMessage(), null);
  }

  // 요청 파라미터, 경로 타입 오류 처리
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException exception) {
    return buildResponse(CommonErrorCode.BAD_REQUEST, exception.getMessage(), null);
  }

  // 인증 실패 (로그인 필요, 401 error)
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException exception) {
//        return buildResponse(CommonErrorCode.UNAUTHORIZED, exception.getMessage(), null);
//    }
//
//    // 인가 실패 (로그인은 했지만, 권한 없음, 403 error)
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
//        return buildResponse(CommonErrorCode.FORBIDDEN, exception.getMessage(), null);
//    }

  // 그 외 모든 예외, nullpointer와 같은 것들..
  // 500번 에러 코드 사용
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
    return buildResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, exception.getMessage(), null);
  }

  // fieldErrors(하나의 요청에 여러 오류 발생)일 경우, 래핑하여 오류 메시지 자동 작성
  private ResponseEntity<ApiResponse<?>> buildResponse(
      BaseErrorCode errorCode,
      String overrideMessage,
      Map<String, String> fieldErrors
  ) {
    BaseErrorCode responseCode = applyOverrideMessage(errorCode, overrideMessage);
    Map<String, Object> result = fieldErrors == null ? null : wrapFieldErrors(fieldErrors);
    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(ApiResponse.onFailure(responseCode, result));
  }

  private BaseErrorCode applyOverrideMessage(BaseErrorCode errorCode, String overrideMessage) {
    if (overrideMessage == null || overrideMessage.isBlank() || overrideMessage.equals(
        errorCode.getMessage())) {
      return errorCode;
    }
    return new BaseErrorCode() {
      @Override
      public org.springframework.http.HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
      }

      @Override
      public String getCode() {
        return errorCode.getCode();
      }

      @Override
      public String getMessage() {
        return overrideMessage;
      }
    };
  }

  private Map<String, Object> wrapFieldErrors(Map<String, String> fieldErrors) {
    Map<String, Object> result = new HashMap<>();
    result.put("fieldErrors", fieldErrors);
    return result;
  }

  private String resolveFieldErrorMessage(FieldError fieldError) {
    return Optional.ofNullable(fieldError.getDefaultMessage()).orElse("요청이 올바르지 않습니다.");
  }

  private String resolveConstraintField(ConstraintViolation<?> violation) {
    String path = violation.getPropertyPath().toString();
    int lastDot = path.lastIndexOf('.');
    return lastDot >= 0 ? path.substring(lastDot + 1) : path;
  }

  private String resolveOverrideMessage(ApiException exception, BaseErrorCode errorCode) {
    String message = exception.getMessage();
    if (message == null || message.isBlank() || message.equals(errorCode.getMessage())) {
      return null;
    }
    return message;
  }

  private String mergeMessages(String first, String second) {
    if (first == null || first.isBlank()) {
      return second;
    }
    if (second == null || second.isBlank() || first.equals(second)) {
      return first;
    }
    return first + "; " + second;
  }
}