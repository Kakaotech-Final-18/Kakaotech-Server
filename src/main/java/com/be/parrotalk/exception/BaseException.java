package com.be.parrotalk.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception 처리를 해야 할 때
 * throw new BaseException(ErrorCode.에러이름);
 * 으로 활용해주시면 됩니다.
 */
@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

}