package com.example.ticketing.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long userId;

    @Column(unique = true, nullable = false, length = 50, name = "id")
    private String loginId;

    @Column(nullable = false, length = 255)
    private String pw;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 100)
    private String email;

    @Builder
    public User(String loginId, String pw, String userName, String email) {
        this.loginId = loginId;
        this.pw = pw;
        this.userName = userName;
        this.email = email;
    }

    // 💡 비밀번호 변경 메서드
    public void updatePassword(String encodedNewPw) {
        this.pw = encodedNewPw;
    }

    // 💡 내 정보(이름, 이메일) 변경 메서드
    public void updateInfo(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }
}