package com.be.parrotalk.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 각 서비스별로 발생하는 에러를 커스텀으로 설정하는 클래스입니다.
 */

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 인증 에러
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    NO_MAIL_AUTH_CODE(HttpStatus.NOT_FOUND, "해당 이메일로 요청된 인증 코드가 존재하지 않습니다."),
    MAIL_AUTH_CODE_INCORRECT(HttpStatus.BAD_REQUEST, "입력한 이메일 인증 번호가 틀립니다."),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),

    /**
     * 멤버 에러
     */
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "사용자 로그인에 실패했습니다."),
    BAD_INPUT(HttpStatus.BAD_REQUEST, "입력 형식이 잘못되었습니다."),
    NO_USER_INFO(HttpStatus.NOT_FOUND, "사용자 정보가 존재하지 않습니다."),
    EXISTING_USER_INFO(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),

    /**
     * 토큰 에러
     */
    NO_TOKEN_CONTENT(HttpStatus.BAD_REQUEST, "토큰의 내용을 가져오지 못했습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    TOKEN_CATEGORY_INCORRECT(HttpStatus.BAD_REQUEST, "토큰의 종류가 맞지 않습니다."),

    /**
     * 1대1 통화방 에러
     */
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 room id로 방을 찾을 수 없습니다."),

    /**
     * 일반 오류 코드
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP Method 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다.");

    private final HttpStatus status;
    private final String message;

}