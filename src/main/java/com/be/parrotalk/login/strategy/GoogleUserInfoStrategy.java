package com.be.parrotalk.login.strategy;

import com.be.parrotalk.login.dto.OAuth2Response;
import com.be.parrotalk.login.util.ProviderType;

import java.util.Map;

public class GoogleUserInfoStrategy implements OAuth2UserInfoStrategy {

    @Override
    public OAuth2Response extractUserInfo(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String nickName = (String) attributes.get("name");
        String profileImage = (String) attributes.get("picture");

        return new OAuth2Response(email, nickName, profileImage, ProviderType.GOOGLE);
    }
}
