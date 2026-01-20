package com.ssafy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 잘못된 요청일 때 발생하는 예외.
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    public BadRequestException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
