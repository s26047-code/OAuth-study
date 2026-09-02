package com.oauthstudy.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken {

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private Long userId;

    private LocalDateTime expiryDate;

    @Builder
    public RefreshToken(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = LocalDateTime.now()
                .plusDays(REFRESH_TOKEN_EXPIRATION_DAYS); //자동 계산 로직
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public void updateToken(String newToken) {
        this.token = newToken;
        this.expiryDate = LocalDateTime.now()
                .plusDays(REFRESH_TOKEN_EXPIRATION_DAYS);
    }
}