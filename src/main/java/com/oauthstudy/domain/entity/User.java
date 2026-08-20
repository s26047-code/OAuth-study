package com.oauthstudy.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String provider;
    private String providerId; //OAuth 제공자가 주는 고유 ID

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String email, String name, String provider,String providerId, Role role) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }
}
