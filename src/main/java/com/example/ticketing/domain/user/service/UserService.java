package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.user.dto.LoginRequestDTO;
import com.example.ticketing.domain.user.dto.SignUpRequestDTO;
import com.example.ticketing.domain.user.dto.TokenResponseDTO;
import com.example.ticketing.domain.user.entity.RefreshToken;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByLoginId(requestDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다."));

        if (!passwordEncoder.matches(requestDTO.getPw(), user.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 1. 두 개의 토큰 생성
        String accessToken = jwtUtil.createToken(user.getUserId(), user.getLoginId());
        String refreshToken = jwtUtil.createRefreshToken();

        // 2. Refresh Token을 DB에 저장하거나 업데이트
        refreshTokenRepository.findByLoginId(user.getLoginId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken), // 이미 있으면 값만 업데이트
                        () -> refreshTokenRepository.save(new RefreshToken(user.getLoginId(), refreshToken)) // 없으면 새로 생성
                );

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String loginId) {
        // DB에서 해당 사용자의 Refresh Token을 삭제하여 무효화합니다.
        refreshTokenRepository.deleteByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public TokenResponseDTO refreshToken(String refreshToken) {
        // 1. 넘어온 Refresh Token이 정상인지 확인
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        // 2. DB에 저장된 토큰인지 확인 (탈취당한 토큰 방어)
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Refresh Token입니다."));

        // 3. 사용자를 찾아 새로운 Access Token 발급
        User user = userRepository.findByLoginId(savedToken.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.createToken(user.getUserId(), user.getLoginId());

        // Refresh Token은 그대로 유지하고 Access Token만 갱신해서 반환
        return new TokenResponseDTO(newAccessToken, refreshToken);
    }
}