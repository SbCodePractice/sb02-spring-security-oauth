package com.sb02.practice.security.controller;

import com.sb02.practice.security.service.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /**
     * 홈 페이지
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 대시보드 (로그인 필요)
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, Model model) {
        if (customOAuth2User != null) {
            model.addAttribute("userId", customOAuth2User.getUserId());
            model.addAttribute("name", customOAuth2User.getDisplayName());
            model.addAttribute("email", customOAuth2User.getEmail());
            model.addAttribute("picture", customOAuth2User.getPictureUrl());
            model.addAttribute("provider", customOAuth2User.getUser().getProvider());
            model.addAttribute("createdAt", customOAuth2User.getUser().getCreatedAt());
        }
        return "dashboard";
    }
}