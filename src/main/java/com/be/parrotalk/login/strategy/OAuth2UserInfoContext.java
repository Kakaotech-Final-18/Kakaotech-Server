package com.be.parrotalk.login.strategy;

import com.be.parrotalk.login.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class OAuth2UserInfoContext {

    private final OAuth2UserInfoStrategy strategy;

    public OAuth2Response extractUserInfo(Map<String, Object> attributes) {
        return strategy.extractUserInfo(attributes);
    }
}
