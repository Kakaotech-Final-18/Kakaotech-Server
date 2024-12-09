package com.be.parrotalk.login.oauth2;

import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.service.AuthService;
import com.be.parrotalk.login.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final RedisService redisService;

    @Value("${front.url}")
    private String frontUrl;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenExpirationTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        TokenResponseDto tokenResponse = authService.createJwtTokens(authentication);

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(tokenResponse.getUserInfo().getId().toString(), tokenResponse.getRefreshToken(), Duration.ofDays(7));

        // RefreshToken을 쿠키에 저장
        createCookie("refresh", tokenResponse.getRefreshToken(), (int) refreshTokenExpirationTime, response, request);

        // 클라이언트가 적절한 경로로 리다이렉트
        String redirectUrl = frontUrl + "/call/home";

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }


    public void createCookie(String key, String value, int maxAge, HttpServletResponse response, HttpServletRequest request) {
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(isSecureEnvironment()) // HTTPS 사용 시 Secure=true
//                .sameSite("None") // Cross-Domain 시 None 필요
                .build();

        // 응답에 Set-Cookie 헤더로 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }


    private boolean isSecureEnvironment() {
        // 개발/운영 환경 분리
        return "production".equals(System.getenv("ENV"));
    }
}