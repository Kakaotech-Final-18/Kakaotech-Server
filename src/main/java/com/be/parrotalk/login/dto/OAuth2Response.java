package com.be.parrotalk.login.dto;

import com.be.parrotalk.login.util.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2Response {
    private String email;
    private String nickName;
    private String profileImage;
    private ProviderType provide;
}