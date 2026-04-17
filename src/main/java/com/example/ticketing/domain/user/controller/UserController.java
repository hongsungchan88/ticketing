package com.example.ticketing.domain.user.controller;

import com.example.ticketing.domain.user.dto.LoginRequestDTO;
import com.example.ticketing.domain.user.dto.SignUpRequestDTO;
import com.example.ticketing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor // final이 붙은 핃드의 생성자를 자동으로 만들어 의존성을 주입한다.
public class UserController {
    private final UserService userService;

    @PostMapping("/users/signup") // 회원가입
    public ResponseEntity<Void> singUp(@RequestBody @Valid SignUpRequestDTO requestDTO) {
        log.info("회원가입 요청 ID: " + requestDTO.getLoginId());
        userService.signUp(requestDTO);
        log.info("회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users/login") // 로그인
    public ResponseEntity<Long> login(@RequestBody @Valid LoginRequestDTO requestDTO) {
        log.info("로그인 시도한 ID: " + requestDTO.getLoginId());
        Long userId = userService.login(requestDTO);
        log.info("로그인 성공");
        return ResponseEntity.ok(userId);
    }
}