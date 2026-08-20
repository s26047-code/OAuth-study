package com.oauthstudy.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends RuntimeException {

    private final HttpStatus status;

    public InvalidTokenException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getStatus() {
        return status;
    }
}