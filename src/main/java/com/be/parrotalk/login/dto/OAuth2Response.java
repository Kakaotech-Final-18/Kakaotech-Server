package com.be.parrotalk.login.dto;

import com.be.parrotalk.login.domain.ProviderType;

import java.util.Map;

public class OAuth2Response {

    private final Map<String, Object> attribute;

    public OAuth2Response(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
    }

    public ProviderType getProvider() {
        return ProviderType.KAKAO;
    }

    public String getEmail() {
        return attribute.get("email").toString();
    }

    public String getNickName() {
        Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");
        return profile.get("nickname").toString();
    }

    public String getProfileImage() {
        Map<String, Object> profile = (Map<String, Object>) attribute.get("profile");
        return profile.get("profile_image_url").toString();
    }

}