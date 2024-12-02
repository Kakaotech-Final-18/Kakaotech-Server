package com.be.parrotalk.login.dto;

import lombok.Builder;

@Builder
public class UserInfoDTO {
    private String nickname;
    private String email;
    private String profileImage;
}