package com.be.parrotalk.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String nickname;
    private String email;
    private String profileImage;
}