package com.be.parrotalk.login.controller;

import com.be.parrotalk.login.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 후 home이 랜더링 될 때 access token을 재생성 후 전달
     */
    @PostMapping("/access")
    public ResponseEntity<?> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // AuthService로 로직 위임
            String accessToken = authService.processAccessTokenRefresh(request, response);

            // 응답 헤더에 Access Token 추가
            response.setHeader("Authorization", "Bearer " + accessToken);

            return ResponseEntity.ok("Access token refreshed successfully.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while refreshing access token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    /**
     * Refresh Token을 사용하여 JWT Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Refresh Token을 사용하여 새로운 Access Token 발급
            authService.refreshJwtTokens(request, response);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}