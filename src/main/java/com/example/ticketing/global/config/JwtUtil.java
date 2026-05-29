package com.example.ticketing.global.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // 스프링 빈으로 등록하여 어디서든 주입받아 쓸 수 있게 합니다.
public class JwtUtil {

    // application.yml에 적어둔 비밀키를 가져옵니다.
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 만료 시간 설정
    private final long tokenValidTime = 1000L * 60 * 60;

    // 💡 1. Refresh Token 만료 시간 추가
    private final long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7;

    // JWT 토큰 생성 메서드
    public String createToken(Long userId, String loginId) {
        // 비밀키를 암호화 알고리즘에 맞는 Key 객체로 변환
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(loginId) // 토큰의 주인(주제)
                .claim("userId", userId) // 토큰에 담을 추가 정보 (예매할 때 편하도록 PK를 담아둡니다)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키를 사용해 HS256 알고리즘으로 서명(도장 쾅!)
                .compact();
    }

    // 토큰에서 로그인 ID 추출
    public String getLoginId(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰이 위조되지 않았고, 만료되지 않았는지 정상 여부 확인
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 교환권에는 개인정보(loginId)를 담지 않고 순수하게 유효기간만 설정하여 발급합니다.
    public String createRefreshToken() {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}