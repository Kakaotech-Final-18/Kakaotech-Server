package com.be.parrotalk.login.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.CustomOAuth2User;
import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void refreshJwtTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenInCookie(request);
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

    public TokenResponseDto reissueAccess(String refresh, HttpServletResponse response) {


        String userId = jwtTokenProvider.getUserId(refresh);

        // 새로운 JWT 생성
        String newAccess = jwtTokenProvider.createAccessToken(userId);

        // 응답 설정
        response.setHeader("Authorization", "Bearer " + newAccess);

        return new TokenResponseDto(newAccess, refresh, null);
    }

    public ResponseEntity<String> validateRefreshToken(String refresh) {
        //refresh null check
        if (refresh == null) {
            return new ResponseEntity<>("refresh value null", HttpStatus.BAD_REQUEST);
        }

        // expired check
        try {
            jwtTokenProvider.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        //db check
        String userId = jwtTokenProvider.getUserId(refresh);
        String storedRefreshToken = redisService.getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            if (!storedRefreshToken.equals(refresh)) {
                redisService.deleteRefreshToken(storedRefreshToken);
            }
            return new ResponseEntity<>("refresh db check", HttpStatus.BAD_REQUEST);
        }

        return null; // 유효성 검사를 통과한 경우 null 반환
    }

    public String processAccessTokenRefresh(HttpServletRequest request, HttpServletResponse response) {
        // Refresh Token 쿠키 가져오기
        String refreshToken = getRefreshTokenInCookie(request);

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token not found.");
        }

        // Refresh Token 검증
        validateRefreshToken(refreshToken);

        // Access Token 생성
        TokenResponseDto tokenResponse = reissueAccess(refreshToken, response);

        return tokenResponse.getAccessToken();
    }

    private static String getRefreshTokenInCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("No cookies found.");
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        return refreshToken;
    }
}