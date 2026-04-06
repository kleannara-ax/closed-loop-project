package com.company.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 비즈니스 예외
 * ※ 수정 금지 대상
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(String message) {
        this(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", message);
    }

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
