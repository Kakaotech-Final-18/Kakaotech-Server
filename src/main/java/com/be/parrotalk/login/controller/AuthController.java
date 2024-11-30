package com.be.parrotalk.login.controller;

import com.be.parrotalk.login.dto.TokenRefreshRequest;
import com.be.parrotalk.login.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/oauth2/authorization")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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