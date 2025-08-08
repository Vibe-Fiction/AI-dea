package com.spring.aidea.vibefiction.global.exception;

import lombok.Getter;

/*
 *  범용적인 에러가 아니라 우리 앱에서만 발생하는 독특한 에러들을 저장하는 예외클래스
 *
 * */
@Getter
public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;

    }
}