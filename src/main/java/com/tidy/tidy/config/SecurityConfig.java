// com.tidy.tidy.config.SecurityConfig
package com.tidy.tidy.config;

import com.tidy.tidy.config.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@RequiredArgsConstructor

public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/css/**", "/images/**", "/js/**",
                                "/oauth2/authorization/**", "/login/oauth2/code/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google") // 선택: 직접 링크 사용
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("http://localhost:3000/", true) // 로그인 성공 시 홈으로
                )

                .logout(logout -> logout
                        .logoutUrl("/auth/logout")        // 이 URL로 POST 요청이 들어오면 로그아웃 처리
                        .logoutSuccessUrl("/")            // 로그아웃 후 이동할 페이지
                        .deleteCookies("JSESSIONID")      // 세션 쿠키 삭제
                        .invalidateHttpSession(true)      // 세션 무효화
                        .clearAuthentication(true)        // SecurityContext 정리
                )

                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable()); // H2 콘솔 쓸 경우 등

        return http.build();
    }
}
