package com.oauthstudy.domain.controller;

import com.oauthstudy.domain.entity.RefreshToken;
import com.oauthstudy.domain.repository.RefreshTokenRepository;
import com.oauthstudy.global.exception.InvalidTokenException;
import com.oauthstudy.global.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Transactional //DB갱신
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 재발급 토큰입니다.");
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("존재하지 않는 토큰입니다. 다시 로그인해주세요."));

        if (savedToken.isExpired()) {
            throw new InvalidTokenException("만료된 토큰입니다. 다시 로그인해주세요.");
        }

        Long userId = savedToken.getUserId();
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        savedToken.updateToken(newRefreshToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        ));
    }
}