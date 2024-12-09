package com.be.parrotalk.login.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.jsonwebtoken.security.Keys.*;


@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private Key key;
    private final static int BEARERTOKEN_START_NUMBER = 7;

    @PostConstruct
    protected void init() {
        this.key = hmacShaKeyFor(Base64.getEncoder().encode(secretKey.getBytes()));
    }

    private String createToken(String userId, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("roles", List.of("USER")); // 사용자 권한 정보 추가

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Access Token 생성
     */
    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidity);
    }

    /**
     * JWT Refresh Token 생성
     */
    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidity);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = claims.get("roles", List.class); // "roles" 클레임에서 권한 정보 추출
        return roles.stream()
                .map(SimpleGrantedAuthority::new) // 권한 정보를 GrantedAuthority로 변환
                .collect(Collectors.toList());
    }


    /**
     * JWT 토큰에서 인증 정보 조회
     */
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token); // JWT에서 사용자 ID 추출
        List<GrantedAuthority> authorities = getAuthorities(token); // JWT에서 권한 정보 추출

        // UserDetails 생성
        User userDetails = new User(userId, "", authorities);
        com.be.parrotalk.login.domain.User userInfo = new com.be.parrotalk.login.domain.User();
        userInfo.setId(Long.parseLong(userId));

        // Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(userInfo, "", userDetails.getAuthorities());
    }


    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰 유효성 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰의 만료 시간 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
            return expiration.before(new Date()); //기한 날짜가 현재 날짜보다 전이면 true, 아니면 false
        } catch (Exception e) {
            log.error("JWT 토큰 만료 확인 중 오류 발생: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Request Header에서 토큰 가져오기
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(BEARERTOKEN_START_NUMBER).trim();
            if (!token.isEmpty()) {
                return token;
            } else {
                log.warn("Authorization 헤더에 빈 토큰이 포함되어 있습니다.");
            }
        } else if (bearerToken != null) {
            log.warn("Authorization 헤더 형식이 잘못되었습니다. 값: {}", bearerToken);
        } else {
            log.info("Authorization 헤더가 요청에 포함되지 않았습니다.");
        }
        return null;
    }
}