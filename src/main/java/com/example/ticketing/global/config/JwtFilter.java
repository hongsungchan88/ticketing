package com.example.ticketing.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 'Authorization' 값을 꺼내옵니다.
        final String authorizationHeader = request.getHeader("Authorization");

        // 2. 토큰이 없거나, 'Bearer '로 시작하지 않으면 검색대를 그냥 통과시킵니다.
        // (이 경우 토큰이 없으므로 SecurityConfig의 설정에 따라 예매 API 등에서 403 에러가 터집니다)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 순수 토큰 문자열만 추출 ("Bearer " 뒷부분)
        String token = authorizationHeader.substring(7);

        // 4. 토큰이 유효한지 검사
        if (jwtUtil.validateToken(token)) {
            // 5. 유효하다면 토큰에서 로그인 ID를 꺼냅니다.
            String loginId = jwtUtil.getLoginId(token);
            log.info("인증된 사용자 요청: {}", loginId);

            // 6. 스프링 시큐리티에게 "이 사용자는 인증되었음!" 이라고 알려주는 강제 인증 객체를 생성합니다.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginId, null, List.of(new SimpleGrantedAuthority("USER")));

            // 7. 서버의 SecurityContext 공간에 인증 객체를 저장합니다. (이제 문이 열립니다!)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}