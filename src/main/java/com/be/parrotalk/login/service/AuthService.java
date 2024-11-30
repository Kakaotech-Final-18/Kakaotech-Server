package com.be.parrotalk.login.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.CustomOAuth2User;
import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    /**
     * 현재 인증된 사용자를 기준으로 JWT Access Token 및 Refresh Token 생성
     */
    public TokenResponseDto createJwtTokens(Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            throw new RuntimeException("OAuth2 사용자 정보가 없습니다.");
        }
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUserDetails.getUserId();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

        return new TokenResponseDto(accessToken, refreshToken, user);
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    public void refreshJwtTokens(HttpServletResponse response, String refreshToken) {
        String userId = jwtTokenProvider.getUserId(refreshToken);

        // Refresh Token이 Redis에 존재하는지 확인
        String storedRefreshToken = redisService.getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            if (storedRefreshToken != null) {
                redisService.deleteRefreshToken(storedRefreshToken);
            }
            throw new IllegalArgumentException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Refresh Rotation
        redisService.deleteRefreshToken(storedRefreshToken);
        log.info("Redis에 Refresh Token 저장: userId={}, refreshToken={}", userId, newRefreshToken);
        redisService.saveRefreshToken(userId, newRefreshToken, Duration.ofDays(7));

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken, Duration.ofDays(7)));
    }

    private Cookie createCookie(String key, String value, Duration duration) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) duration.getSeconds());
        cookie.setSecure(isSecureEnvironment());
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private boolean isSecureEnvironment() {
        // 개발/운영 환경 분리
        return "production".equals(System.getenv("ENV"));
    }
}