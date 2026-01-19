package com.ssafy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-resources/**").permitAll()
                // 인증 없이 접근 가능
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/users").permitAll()
                // 퀴즈 코스도 로그인 없이 문제 풀 수 있으므로 api 추가
                .requestMatchers(HttpMethod.GET, "/api/v1/quiz-courses", "/api/v1/quiz-courses/").permitAll()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
=======
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 일단 모든 요청 허용
                        .anyRequest().permitAll()
                );
        // JWT 필터 제거
>>>>>>> origin/study

        return http.build();
    }
}