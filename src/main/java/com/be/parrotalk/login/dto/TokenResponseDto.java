package com.be.parrotalk.login.dto;


import com.be.parrotalk.login.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private User userInfo;
}