package com.be.parrotalk.config;

import com.be.parrotalk.login.oauth2.CustomSuccessHandler;
import com.be.parrotalk.login.security.CustomLogoutFilter;
import com.be.parrotalk.login.security.JwtAuthenticationFilter;
import com.be.parrotalk.login.security.JwtTokenProvider;
import com.be.parrotalk.login.service.CustomOAuth2UserService;
import com.be.parrotalk.login.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RedisService redisService;
    private final CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 생성 정책 설정
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(WHITE_LIST_URL).permitAll() // 공개 URL
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler) // OAuth2 성공 핸들러
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), OAuth2LoginAuthenticationFilter.class) // JWT 인증 필터
                .addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, redisService), LogoutFilter.class) // 커스텀 로그아웃 필터
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true); // 쿠키 허용
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    private static final String[] WHITE_LIST_URL = {
        "/", "/index",
        "/login/oauth2/code/**",
        "/oauth2/**",
        "/api/v1/auth/access",
        "/favicon.ico",

        // swagger
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
    };
}
