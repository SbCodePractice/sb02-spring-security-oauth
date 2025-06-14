package com.sb02.practice.security.entity;

import lombok.Getter;

@Getter
public enum Provider {
    GOOGLE("google");

    private final String registrationId;

    Provider(String registrationId) {
        this.registrationId = registrationId;
    }

    public static Provider fromRegistrationId(String registrationId) {
        for (Provider provider : values()) {
            if (provider.registrationId.equals(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + registrationId);
    }
}