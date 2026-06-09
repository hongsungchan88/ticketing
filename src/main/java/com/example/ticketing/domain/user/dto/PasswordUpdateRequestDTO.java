package com.example.ticketing.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequestDTO {
    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    private String oldPw;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    private String newPw;
}