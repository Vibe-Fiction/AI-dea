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
     * API 요청이 실패했을 때, 표준 형식의 실패 응답을 생성하는 정적 팩토리 메서드입니다.
     *
     * <b>[사용 목적]</b>
     * 이 메서드를 통해 애플리케이션의 모든 API 실패 응답이 일관된 구조({@code success}, {@code message}, {@code data}, {@code timestamp})를
     * 갖도록 보장합니다. 이는 클라이언트 측에서의 에러 핸들링 로직을 표준화하고 단순화하는 데 도움을 줍니다.
     *
     * @param <T>     응답 데이터의 제네릭 타입. 실패 응답의 경우, data 필드는 항상 {@code null}이 됩니다.
     * @param message 클라이언트에게 전달하거나 로깅할 실패 원인에 대한 명확한 설명.
     * @return {@code success} 필드가 {@code false}로 설정된 새로운 {@link ApiResponse} 인스턴스.
     * @author 왕택준
     * @since 2025.08
     */
    public static <T> ApiResponse<T> failure(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now())
            .data(null)
            .build();
    }

}
