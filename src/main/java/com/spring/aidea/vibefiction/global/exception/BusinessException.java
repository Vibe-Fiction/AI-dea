package com.spring.aidea.vibefiction.global.exception;

import lombok.Getter;

/*
 *  범용적인 에러가 아니라 우리 앱에서만 발생하는 독특한 에러들을 저장하는 예외클래스
 *
 * */
@Getter
public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

//    public BusinessException(String message) {
//        super(message);
//    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;

    }

    /**
     * 지정된 ErrorCode와 함께, 기본 메시지를 대체하는 상세 메시지를 사용하여 {@code BusinessException}을 생성합니다.
     * <p>
     * <b>[사용 시나리오]</b>
     * 이 생성자는 {@link ErrorCode}에 정의된 기본 메시지로는 상황을 충분히 설명할 수 없을 때,
     * 동적인 데이터(예: 특정 ID 값)를 포함한 구체적인 오류 메시지를 전달하고자 할 때 유용합니다.
     *
     * @param errorCode 발생한 에러의 종류를 정의하는 {@link ErrorCode}. 이 코드를 통해 클라이언트는 에러 유형을 기계적으로 파악할 수 있습니다.
     * @param message   해당 {@code ErrorCode}의 기본 메시지를 덮어쓰고, 클라이언트나 로그에 표시될
     *                  상세 에러 메시지입니다. (예: "ID 'user123'에 해당하는 사용자를 찾을 수 없습니다.")
     * @author 왕택준
     * @since 2025.08
     */
    public BusinessException(ErrorCode errorCode, String message) {
        // [설계] 전달된 상세 메시지는 상위 예외 클래스로 넘겨 예외의 기본 메시지(e.getMessage())로 사용
        super(message);
        // [설계] ErrorCode는 이 예외 객체 내에 보관하여, 전역 예외 핸들러에서 HTTP 상태 코드나 응답 본문을 구성하는 데 사용
        this.errorCode = errorCode;
    }

}
