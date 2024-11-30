package com.be.parrotalk.login.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.ProviderType;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.CustomOAuth2User;
import com.be.parrotalk.login.dto.OAuth2Response;
import com.be.parrotalk.login.dto.UserDTO;
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

        if (!KAKAO_REGISTRATION_ID.equals(registrationId) && !GOOGLE_REGISTRATION_ID.equals(registrationId)) {
            log.error("지원되지 않는 registrationId: {}", registrationId);
            throw new OAuth2AuthenticationException("잘못된 registration id 입니다.");
        }
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2Response oAuth2Response = new OAuth2Response(oAuth2User.getAttributes());
        return processUser(oAuth2Response, registrationId);
    }

    /**
     * 사용자 정보 처리
     */
    private OAuth2User processUser(OAuth2Response oAuth2Response, String registrationId) {
        User user = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(() -> createUser(oAuth2Response, registrationId));

        UserDTO userDTO = convertToDTO(user);
        return new CustomOAuth2User(userDTO);
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

    /**
     * User -> UserDTO 변환
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .profileImage(user.getProfileImage())
                .build();
    }
}