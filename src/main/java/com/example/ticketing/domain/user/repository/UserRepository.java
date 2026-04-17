package com.example.ticketing.domain.user.repository;

import com.example.ticketing.domain.user.entity.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ID 찾기
    // Optional<T> : NPE 방지, NPE : Null Pointer Exception
    Optional<User> findByLoginId(String loginId);

    // ID 중복 검사
    boolean existsByLoginId(@Nonnull String id);
}