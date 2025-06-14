package com.sb02.practice.security.service;

import com.sb02.practice.security.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2User와 데이터베이스 User 엔티티를 결합한 커스텀 구현체
 */
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;

    @Getter
    private final User user;

    public CustomOAuth2User(OAuth2User oauth2User, User user) {
        this.oauth2User = oauth2User;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 기본적으로 ROLE_USER 권한 부여
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    /**
     * 사용자 ID 반환
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 사용자 이메일 반환
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 사용자 이름 반환
     */
    public String getDisplayName() {
        return user.getName();
    }

    /**
     * 프로필 이미지 URL 반환
     */
    public String getPictureUrl() {
        return user.getPictureUrl();
    }
}