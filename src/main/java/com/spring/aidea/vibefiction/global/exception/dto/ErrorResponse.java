package com.spring.aidea.vibefiction.global.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 값이 null인 프로퍼티는 응답에서 제외 (검증오류와 중복오류의 출력값의 종류가 다르기때문에 필요함)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    // 에러 발생 시간
    private LocalDateTime timestamp;

    // http status code
    private int status;  // ex: 404, 400, 403, 500...

    // 에러 코드 이름
    private String error; // ex: bad request, not found...

    // 상세 에러 정보
    private String detail;

    // 에러가 난 URL path
    private String path;

    // 유효성 검증 에러 목록
    private List<ValidationError> validationErrors;


    // 입력값 검증 오류 1개를 포장할 내부 클래스
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        // 에러가 난 필드명
        private String field;

        // 에러 원인 메시지
        private String message;

        // 거부된 값 (클라이언트가 뭐라고 보냈는지)
        private Object rejectedValue;
    }
}