package com.example.ticketing.domain.user.repository;

import com.example.ticketing.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 로그인 ID로 교환권 찾기
    Optional<RefreshToken> findByLoginId(String loginId);

    // 토큰 값으로 교환권 찾기 (나중에 재발급할 때 사용)
    Optional<RefreshToken> findByToken(String token);

    // 로그아웃 시 교환권 삭제
    void deleteByLoginId(String loginId);
}