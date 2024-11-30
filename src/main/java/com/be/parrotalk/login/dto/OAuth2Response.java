package com.be.parrotalk.login.dto;

import com.be.parrotalk.login.domain.ProviderType;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Response {

    private String email;
    private String nickName;
    private String profileImage;
    private ProviderType provide;

    public OAuth2Response(Map<String, Object> attributes) {
        if (attributes.containsKey("kakao_account")) { // Kakao
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            this.email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            this.nickName = (String) profile.get("nickname");
            this.profileImage = (String) profile.get("profile_image_url");
            this.provide = ProviderType.KAKAO;
        } else if (attributes.containsKey("email")) { // Google
            this.email = (String) attributes.get("email");
            this.nickName = (String) attributes.get("name");
            this.profileImage = (String) attributes.get("picture");
            this.provide = ProviderType.GOOGLE;
        }
    }
}