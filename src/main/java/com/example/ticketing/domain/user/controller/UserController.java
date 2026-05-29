package com.example.ticketing.domain.user.controller;

import com.example.ticketing.domain.user.dto.LoginRequestDTO;
import com.example.ticketing.domain.user.dto.SignUpRequestDTO;
import com.example.ticketing.domain.user.dto.TokenResponseDTO;
import com.example.ticketing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor // final이 붙은 핃드의 생성자를 자동으로 만들어 의존성을 주입한다.
public class UserController {
    private final UserService userService;

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequestDTO requestDTO) {
        log.info("회원가입 요청 ID: " + requestDTO.getLoginId());
        userService.signUp(requestDTO);
        log.info("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO requestDTO) {
        TokenResponseDTO tokenResponse = userService.login(requestDTO);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        String loginId = authentication.getName(); // 토큰에서 현재 로그인한 ID 추출
        userService.logout(loginId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        TokenResponseDTO tokenResponse = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}