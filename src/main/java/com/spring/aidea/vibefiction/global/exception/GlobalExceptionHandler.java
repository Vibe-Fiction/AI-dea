package com.spring.aidea.vibefiction.global.exception;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.spring.aidea.vibefiction.global.exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 전역 예외처리 핸들러
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리
 */
@Slf4j
@RestControllerAdvice // AOP : 관점지향 프로그래밍
public class GlobalExceptionHandler {

    /**
     * 우리 앱에서 발생한 커스텀 예외들을 처리
     * @ExceptionHandler - 우리 앱에서 throw된 에러들을 처리할 예외클래스
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(
            BusinessException e
            , HttpServletRequest request
    ) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());

        // 에러 응답객체 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .detail(e.getMessage())
                .path(request.getRequestURI())
                .status(e.getErrorCode().getStatus())
                .error(e.getErrorCode().getCode())
                .build();

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }

    /**
     * 유효성 검증 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<ErrorResponse.ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        log.warn("유효성 검증 실패: {}", validationErrors);

        ErrorResponse response = ErrorResponse.builder()
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .error(ErrorCode.VALIDATION_ERROR.getCode())
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * JSON 파싱 오류 등 요청 형식이 잘못되었을 때 발생하는 예외를 처리합니다.
     * - ex) 회원가입 시 생일 날짜 형식 yyyy-MM-dd -> 2000-1-1로 입력할 경우 예외처리
     * @author 고동현
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        String fieldName = "unknown";
        Object rejectedValue = null; // 사용자가 입력한 값을 담을 변수
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;

            // 1. 사용자가 입력한 값을 가져옵니다.
            rejectedValue = ife.getValue();

            // 2. 에러가 발생한 필드 이름을 가져옵니다.
            fieldName = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));

            // 3. 날짜 형식 오류인지 확인하고 에러 코드를 구체화합니다.
            if (ife.getTargetType() != null && ife.getTargetType().isAssignableFrom(LocalDate.class)) {
                errorCode = ErrorCode.INVALID_DATE_FORMAT;
            }
        }

        // 4. 로그에 필드 이름과 사용자가 입력한 값을 함께 기록합니다.
        log.warn("JSON 파싱 오류 - 필드: [{}], 입력값: [{}], 메시지: {}", fieldName, rejectedValue, e.getMessage());

        List<ErrorResponse.ValidationError> validationErrorList = Collections.singletonList(
                ErrorResponse.ValidationError.builder()
                        .field(fieldName)
                        .message(errorCode.getMessage())
                        .rejectedValue(rejectedValue) // 응답에도 입력값을 담아줍니다.
                        .build()
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .error(ErrorCode.VALIDATION_ERROR.getCode())
                .validationErrors(validationErrorList)
                .build();

        return ResponseEntity.badRequest().body(response);
    }


}