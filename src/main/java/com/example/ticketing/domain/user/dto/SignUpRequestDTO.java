package com.example.ticketing.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDTO { // 회원가입용
    @NotBlank(message = "ID를 필수로 입력해야합니다.") // null, "", " " 모두 허용 거부
    private String loginId;

    @NotBlank(message = "비밀번호를 필수로 입력해야합니다.")
    private String pw;

    @NotBlank(message = "이름을 필수로 입력해야합니다.")
    private String userName;

    @NotBlank(message = "이메일을 필수로 입력해야합니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
}