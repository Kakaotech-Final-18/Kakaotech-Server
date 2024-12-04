package com.be.parrotalk.login.strategy;

import com.be.parrotalk.login.dto.OAuth2Response;
import com.be.parrotalk.login.util.ProviderType;

import java.util.Map;

public class KakaoUserInfoStrategy implements OAuth2UserInfoStrategy {

    @Override
    public OAuth2Response extractUserInfo(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickName = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");

        return new OAuth2Response(email, nickName, profileImage, ProviderType.KAKAO);
    }
}
