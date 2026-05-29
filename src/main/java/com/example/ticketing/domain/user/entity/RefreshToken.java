package com.example.ticketing.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId; // 어떤 사용자의 교환권인지 기억

    @Column(nullable = false)
    private String token; // 실제 Refresh Token 값

    public RefreshToken(String loginId, String token) {
        this.loginId = loginId;
        this.token = token;
    }

    // 💡 기존 교환권을 새 교환권으로 덮어쓸 때 사용하는 메서드
    public void updateToken(String token) {
        this.token = token;
    }
}