package com.be.parrotalk.login.security;

import com.be.parrotalk.login.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        //refresh null check
        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        //expired check
        if (jwtTokenProvider.isTokenExpired(refresh)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //db check
        String userId = jwtTokenProvider.getUserId(refresh);
        String storedRefreshToken = redisService.getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
            if (!storedRefreshToken.equals(refresh)) {
                redisService.deleteRefreshToken(storedRefreshToken);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //logout
        redisService.deleteRefreshToken(userId);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(isSecureEnvironment());

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isSecureEnvironment() {
        // 개발/운영 환경 분리
        return "production".equals(System.getenv("ENV"));
    }
}