package com.example.ticketing.domain.user.dto;

import com.example.ticketing.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private Long userId;
    private String loginId;
    private String userName;
    private String email;

    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.userId = user.getUserId();
        dto.loginId = user.getLoginId();
        dto.userName = user.getUserName();
        dto.email = user.getEmail();
        return dto;
    }
}