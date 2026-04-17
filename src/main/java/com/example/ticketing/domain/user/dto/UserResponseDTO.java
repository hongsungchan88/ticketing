package com.example.ticketing.domain.user.dto;

import com.example.ticketing.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private Long userId;
    private String loginId;
    private String userName;
    private String email;

    // 필요한 데이터만 전송하기 위해 DTO 클래스에서 생성자를 사용해 엔티티를 DTO로 변환하는 방식 사용
    public UserResponseDTO(User user) {
        this.userId = user.getUserId();
        this.loginId = user.getLoginId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
    }
}