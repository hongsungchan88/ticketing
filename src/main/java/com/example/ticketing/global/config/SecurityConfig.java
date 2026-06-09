package com.example.ticketing.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 필터 체인 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // 비밀번호 암호화 객체를 빈으로 등록
    // 회원가입 시 비밀번호를 안전하게 해싱하기 위해 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 규칙 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // 💡 API 경로 별 접근 권한 설정 (이 부분 수정!)
                .authorizeHttpRequests(auth -> auth
                        // 🌟 프론트엔드 HTML 화면과 기본 주소를 누구나 볼 수 있게 허용합니다.
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/error").permitAll()

                        // 기존 API 누구나 접근 가능
                        .requestMatchers("/api/v1/users/signup", "/api/v1/users/login",
                                "/api/v1/performances/**", "/api/v1/users/refresh-token").permitAll()

                        // 반드시 인증을 거쳐야 접근 가능
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}