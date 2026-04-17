package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.user.dto.LoginRequestDTO;
import com.example.ticketing.domain.user.dto.SignUpRequestDTO;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final이 붙은 핃드의 생성자를 자동으로 만들어 의존성을 주입한다.
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequestDTO requestDTO) {
        if (userRepository.existsByLoginId(requestDTO.getLoginId())) {
            // 잘못된 인수, 메서드에 전달된 인수가 예상된 형식이나 범위를 벗어나는 경우 발생
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User user = User.builder()
                .loginId(requestDTO.getLoginId())
                .pw(requestDTO.getPw())
                .userName(requestDTO.getUserName())
                .email(requestDTO.getEmail())
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Long login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByLoginId(requestDTO.getLoginId())
                // Optional Type 예외 처리 방법
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다."));
        if (!user.getPw().equals(requestDTO.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user.getUserId();
    }
}