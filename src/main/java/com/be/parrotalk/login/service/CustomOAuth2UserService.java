package com.be.parrotalk.login.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.strategy.GoogleUserInfoStrategy;
import com.be.parrotalk.login.strategy.KakaoUserInfoStrategy;
import com.be.parrotalk.login.strategy.OAuth2UserInfoContext;
import com.be.parrotalk.login.strategy.OAuth2UserInfoStrategy;
import com.be.parrotalk.login.util.ProviderType;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.CustomOAuth2User;
import com.be.parrotalk.login.dto.OAuth2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private static final String KAKAO_REGISTRATION_ID = "kakao";
    private static final String GOOGLE_REGISTRATION_ID = "google";

    /**
     * OAuth2 인증 후 사용자 정보 처리
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfoStrategy strategy;
        if (KAKAO_REGISTRATION_ID.equals(registrationId)) {
            strategy = new KakaoUserInfoStrategy();
        }  else if (GOOGLE_REGISTRATION_ID.equals(registrationId)) {
            strategy = new GoogleUserInfoStrategy();
        } else {
            throw new OAuth2AuthenticationException("잘못된 registration id 입니다.");
        }
        OAuth2UserInfoContext context = new OAuth2UserInfoContext(strategy);
        OAuth2Response oAuth2Response = context.extractUserInfo(oAuth2User.getAttributes());

        return processUser(oAuth2Response, registrationId);
    }

    /**
     * 사용자 정보 처리
     */
    private OAuth2User processUser(OAuth2Response oAuth2Response, String registrationId) {
        User user = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(() -> createUser(oAuth2Response, registrationId));

        return new CustomOAuth2User(user);
    }

    /**
     * 새로운 사용자 생성
     */
    private User createUser(OAuth2Response oAuth2Response, String registrationId) {
        User user = User.builder()
                .email(oAuth2Response.getEmail())
                .nickname(oAuth2Response.getNickName())
                .provider(ProviderType.valueOf(registrationId.toUpperCase()))
                .profileImage(oAuth2Response.getProfileImage())
                .build();
        return userRepository.save(user);
    }
}