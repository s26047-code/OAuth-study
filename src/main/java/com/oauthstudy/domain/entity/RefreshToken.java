package com.oauthstudy.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private Long userId;

    private LocalDateTime expiryDate;

    @Builder
    public RefreshToken(String token, Long userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public void updateToken(String newtoken, LocalDateTime newExpirDate){ //토큰 재발금
        this.token = newtoken;
        this.expiryDate = newExpirDate;
    }
}
