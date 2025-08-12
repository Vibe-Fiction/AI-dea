package com.spring.aidea.vibefiction.global.common;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 일관적인 응답의 포맷을 위한 객체
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    // 응답 성공 여부
    private boolean success;

    // 응답 메시지
    private String message;

    // 응답 시간
    private LocalDateTime timestamp;

    // 응답 JSON
    private T data;

    // 응답 객체 생성 팩토리 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    /**
     * 실패 응답을 생성하는 팩토리 메서드
     * @param message 실패 메시지
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> failure(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .data(null) // 실패 시에는 데이터가 없습니다.
            .build();
    }
}
