package com.be.parrotalk.login.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}