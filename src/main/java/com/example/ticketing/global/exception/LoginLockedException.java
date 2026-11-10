package com.example.ticketing.global.exception;

public class LoginLockedException extends RuntimeException {
    public LoginLockedException(String message) {
        super(message);
    }
}
