package com.sb02.practice.security.controller;

import com.sb02.practice.security.service.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    /**
     * 현재 로그인한 사용자 정보 반환
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "인증되지 않은 사용자입니다."));
        }

        return ResponseEntity.ok(Map.of(
                "id", customOAuth2User.getUserId(),
                "email", customOAuth2User.getEmail(),
                "name", customOAuth2User.getDisplayName(),
                "pictureUrl", customOAuth2User.getPictureUrl(),
                "provider", customOAuth2User.getUser().getProvider().name(),
                "createdAt", customOAuth2User.getUser().getCreatedAt(),
                "enabled", customOAuth2User.getUser().isEnabled()
        ));
    }

    /**
     * 사용자 프로필 정보 반환 (OAuth 2.0 원본 속성 포함)
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "인증되지 않은 사용자입니다."));
        }

        return ResponseEntity.ok(Map.of(
                "user", Map.of(
                        "id", customOAuth2User.getUserId(),
                        "email", customOAuth2User.getEmail(),
                        "name", customOAuth2User.getDisplayName(),
                        "pictureUrl", customOAuth2User.getPictureUrl(),
                        "provider", customOAuth2User.getUser().getProvider().name()
                ),
                "oauthAttributes", customOAuth2User.getAttributes(),
                "authorities", customOAuth2User.getAuthorities()
        ));
    }
}