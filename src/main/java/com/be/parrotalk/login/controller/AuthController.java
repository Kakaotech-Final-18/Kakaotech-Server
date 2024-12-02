package com.be.parrotalk.login.controller;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.CustomOAuth2User;
import com.be.parrotalk.login.dto.TokenRefreshRequest;
import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.security.JwtTokenProvider;
import com.be.parrotalk.login.service.AuthService;
import com.be.parrotalk.login.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenExpirationTime;

    @PostMapping("/access")
    public ResponseEntity<?> getAccessToken(@RequestParam("userId") String userId, HttpServletResponse response) {
        try {
            // Redis에서 Refresh Token 확인
            String refreshToken = redisService.getRefreshToken(userId);

            ResponseEntity<String> validationResponse = authService.validateRefreshToken(refreshToken);
            if (validationResponse != null) {
                return validationResponse; // 에러가 있을 경우 반환
            }

            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid userId: " + userId));

            // 유저 정보를 기반으로 SecurityContextHolder에 인증 정보 설정
            CustomOAuth2User userDetails = new CustomOAuth2User(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Refresh Token으로 Access Token 재발급
            TokenResponseDto tokenResponse = authService.reissueAccess(refreshToken, response);

            // Refresh Token을 쿠키에 저장
            response.addCookie(createCookie("refresh", tokenResponse.getRefreshToken(), (int) refreshTokenExpirationTime));

            return ResponseEntity.ok("Access token refreshed successfully.");
        } catch (Exception e) {
            log.error("Error while refreshing access token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    private Cookie createCookie(String key, String value, int duration) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(duration);
        cookie.setSecure(true);
        cookie.setDomain("b0b1-14-138-221-58.ngrok-free.app"); // 상위 도메인으로 설정
        cookie.setPath("/"); // 모든 경로에서 접근 가능
        cookie.setHttpOnly(true);
        log.info("cookie 생성"+cookie);

        return cookie;
    }

    /**
     * Refresh Token을 사용하여 JWT Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletResponse response, @RequestBody TokenRefreshRequest request) {
        try {
            // Refresh Token을 사용하여 새로운 Access Token 발급
            authService.refreshJwtTokens(response, request.getRefreshToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}