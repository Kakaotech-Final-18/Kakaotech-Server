package com.be.parrotalk.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(String userId, String refreshToken, Duration duration) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(userId, refreshToken, duration);
        log.info("Saved data - Key: {}, Value: {}", userId, refreshToken);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        log.info("Retrieved data - Key: {}, Value: {}", userId, valueOperations.get(userId)); // 조회한 값 로그로 출력
        return (String) valueOperations.get(userId);
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }
}