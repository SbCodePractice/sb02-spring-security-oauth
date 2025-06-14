package com.sb02.practice.security.service;

import com.sb02.practice.security.entity.Provider;
import com.sb02.practice.security.entity.User;
import com.sb02.practice.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2UserService로 사용자 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // OAuth 2.0 제공자 정보 추출
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.fromRegistrationId(registrationId);

        // 사용자 정보 추출 및 저장
        User user = processOAuth2User(oauth2User, provider);

        // Custom OAuth2User 객체 생성
        return new CustomOAuth2User(oauth2User, user);
    }

    /**
     * OAuth 2.0 사용자 정보를 처리하고 데이터베이스에 저장한다.
     */
    private User processOAuth2User(OAuth2User oauth2User, Provider provider) {
        // 사용자 정보 추출
        UserInfo userInfo = extractUserInfo(oauth2User, provider);

        log.info("OAuth 2.0 사용자 정보: email={}, name={}, provider={}",
                userInfo.email(), userInfo.name(), provider);

        // 기존 사용자 확인
        return userRepository.findByProviderIdAndProvider(userInfo.providerId(), provider)
                .map(existingUser -> updateExistingUser(existingUser, userInfo))
                .orElseGet(() -> createNewUser(userInfo, provider));
    }

    /**
     * OAuth 2.0 제공자별 사용자 정보 추출
     */
    private UserInfo extractUserInfo(OAuth2User oauth2User, Provider provider) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        return switch (provider) {
            case GOOGLE -> extractGoogleUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth 2.0 제공자입니다: " + provider);
        };
    }

    /**
     * Google OAuth 2.0 사용자 정보 추출
     */
    private UserInfo extractGoogleUserInfo(Map<String, Object> attributes) {
        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String pictureUrl = (String) attributes.get("picture");

        if (providerId == null || email == null || name == null) {
            throw new OAuth2AuthenticationException("Google에서 필수 사용자 정보를 가져올 수 없습니다.");
        }

        return new UserInfo(providerId, email, name, pictureUrl);
    }

    /**
     * 기존 사용자 정보 업데이트
     */
    private User updateExistingUser(User existingUser, UserInfo userInfo) {
        boolean updated = false;

        if (!existingUser.getName().equals(userInfo.name())) {
            existingUser.setName(userInfo.name());
            updated = true;
        }

        if (!java.util.Objects.equals(existingUser.getPictureUrl(), userInfo.pictureUrl())) {
            existingUser.setPictureUrl(userInfo.pictureUrl());
            updated = true;
        }

        if (updated) {
            log.info("사용자 정보 업데이트: email={}", userInfo.email());
        }

        return existingUser;
    }

    /**
     * 새로운 사용자 생성
     */
    private User createNewUser(UserInfo userInfo, Provider provider) {
        User newUser = new User(
                userInfo.email(),
                userInfo.name(),
                userInfo.pictureUrl(),
                userInfo.providerId(),
                provider
        );

        User savedUser = userRepository.save(newUser);
        log.info("새로운 사용자 생성: email={}, provider={}", userInfo.email(), provider);

        return savedUser;
    }

    /**
     * 사용자 정보 추출을 위한 record 클래스
     */
    private record UserInfo(
            String providerId,
            String email,
            String name,
            String pictureUrl
    ) {}
}
