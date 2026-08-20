package com.oauthstudy.global.security;

import com.oauthstudy.domain.entity.RefreshToken;
import com.oauthstudy.domain.entity.Role;
import com.oauthstudy.domain.entity.User;
import com.oauthstudy.domain.repository.RefreshTokenRepository;
import com.oauthstudy.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByProviderAndProviderId("google", providerId) //유저 조회, 신규 저장
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .provider("google")
                                .providerId(providerId)
                                .role(Role.USER)
                                .build()
                ));

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId()); //토큰 발급

        refreshTokenRepository.findByUserId(user.getId()) //토큰 갱신
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, LocalDateTime.now().plusDays(14)),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .token(refreshToken)
                                        .userId(user.getId())
                                        .expiryDate(LocalDateTime.now().plusDays(14))
                                        .build()
                        )
                );

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"accessToken\":\"" + accessToken + "\",\"refreshToken\":\"" + refreshToken + "\"}"
        );
    }
}