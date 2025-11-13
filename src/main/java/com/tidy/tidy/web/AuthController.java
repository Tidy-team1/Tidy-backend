package com.tidy.tidy.web;

import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.web.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class AuthController {
    // 로그인 진입점. 로그인 페이지로 리다이렉트
    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/" + provider);
    }

    // 로그인한 사용자 정보 확인
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal CustomOAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(UserResponse.from(principal.getUser()));
    }
}
