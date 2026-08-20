package com.oauthstudy.domain.controller;

import com.oauthstudy.domain.entity.RefreshToken;
import com.oauthstudy.domain.repository.RefreshTokenRepository;
import com.oauthstudy.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 리프레시 토큰입니다."));
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElse(null);

        if (savedToken == null || savedToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "만료되었거나 존재하지 않는 토큰입니다. 다시 로그인해주세요."));
        }

        Long userId = savedToken.getUserId(); //토큰 갱신

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        savedToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(14));

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        ));
    }
}