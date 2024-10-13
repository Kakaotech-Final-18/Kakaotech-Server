package com.be.parrotalk.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Controller에서 리턴타입을 ResponseEntity<BaseResponse<DTO나 리턴할 실질 데이터 클래스>> 로 설정하고
 * return ResponseEntity.ok().body(new BaseResponse<>(ExampleService.functionName(param));
 * 이렇게 활용해주시면 됩니다.
 * extends ResponseEntity<BaseResponse<T>> 로 변경하는 리팩토링 추후에 필요하면 하시면 됩니다.
 * @param <T>
 */
@Getter
@JsonPropertyOrder({"time", "status", "code", "message", "result"})
public class BaseResponse<T> {

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();
    private final HttpStatus status;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    /**
     * 200 OK 일 떄
     */
    public BaseResponse(T result) {
        this.status = HttpStatus.OK;
        this.code = "200 OK";
        this.message = "요청에 성공했습니다.";
        this.result = result;
    }

    /**
     * 200이 아닌 모든 성공 응답 시
     */
    public BaseResponse(HttpStatus status, String code, String message, T result) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.result = result;
    }
}