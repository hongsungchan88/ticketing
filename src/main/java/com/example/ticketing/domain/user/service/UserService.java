package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.user.dto.LoginRequestDTO;
import com.example.ticketing.domain.user.dto.SignUpRequestDTO;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final이 붙은 핃드의 생성자를 자동으로 만들어 의존성을 주입한다.
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signUp(SignUpRequestDTO requestDTO) {
        if (userRepository.existsByLoginId(requestDTO.getLoginId())) {
            // 잘못된 인수, 메서드에 전달된 인수가 예상된 형식이나 범위를 벗어나는 경우 발생
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPw());

        User user = User.builder()
                .loginId(requestDTO.getLoginId())
                .pw(encodedPassword)
                .userName(requestDTO.getUserName())
                .email(requestDTO.getEmail())
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    // 💡 반환 타입을 Long(회원번호)에서 String(JWT 토큰)으로 변경
    public String login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByLoginId(requestDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다."));

        // 💡 암호화된 비밀번호 일치 여부 확인 (평문 vs 암호화된 값 비교)
        if (!passwordEncoder.matches(requestDTO.getPw(), user.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 💡 3. 비밀번호가 맞다면 JwtUtil을 사용해 토큰을 생성하여 반환!
        return jwtUtil.createToken(user.getUserId(), user.getLoginId());
    }
}