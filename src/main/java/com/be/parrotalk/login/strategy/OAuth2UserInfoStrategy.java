package com.be.parrotalk.login.strategy;

import com.be.parrotalk.login.dto.OAuth2Response;

import java.util.Map;

public interface OAuth2UserInfoStrategy {
    OAuth2Response extractUserInfo(Map<String, Object> attributes);
}