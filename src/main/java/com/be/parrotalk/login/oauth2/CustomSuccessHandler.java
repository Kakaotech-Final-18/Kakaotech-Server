package com.be.parrotalk.login.oauth2;

import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.service.AuthService;
import com.be.parrotalk.login.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    @Value("${ngrok.url}")
    private String ngrokUrl;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenExpirationTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        TokenResponseDto tokenResponse = authService.createJwtTokens(authentication);

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(tokenResponse.getUserInfo().getId().toString(), tokenResponse.getRefreshToken(), Duration.ofDays(7));

        // RefreshToken을 쿠키에 저장
        createCookie("refresh", tokenResponse.getRefreshToken(), (int) refreshTokenExpirationTime, response, request);

//        // Access Token을 응답 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());

        // 클라이언트가 적절한 경로로 리다이렉트
        String redirectUrl = ngrokUrl + "/call/home?userId=" + tokenResponse.getUserInfo().getId().toString();

//        log.info("로그인 성공, 리다이렉트 URL: {}", redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }


    public void createCookie(String key, String value, int maxAge, HttpServletResponse response, HttpServletRequest request) {
        ResponseCookie cookie;
        String cookieDomain = request.getServerName().contains("localhost") ? "localhost" : "b0b1-14-138-221-58.ngrok-free.app";
        System.out.println(cookieDomain);
        if(cookieDomain.equals("localhost")){
            cookie = ResponseCookie.from(key, value)
                    .domain(cookieDomain)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(maxAge)
                    .build();
        }
        else {
            cookie = ResponseCookie.from(key, value)
                    .domain(cookieDomain)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(maxAge)
                    .secure(true)
                    .sameSite("None")
                    .build();
        }

        // 응답에 Set-Cookie 헤더로 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private boolean isSecureEnvironment() {
        // 개발/운영 환경 분리
        return "production".equals(System.getenv("ENV"));
    }
}