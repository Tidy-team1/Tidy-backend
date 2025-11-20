// com.tidy.tidy.config.SecurityConfig
package com.tidy.tidy.config;

import com.tidy.tidy.config.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> {

                    // ⭐ dev 환경에서만 Swagger 접근 허용
                    if (isDev()) {
                        auth.requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html"
                        ).permitAll();
                    }

                    auth
                            .requestMatchers(
                                    "/", "/css/**", "/images/**", "/js/**",
                                    "/oauth2/authorization/**", "/login/oauth2/code/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                })

                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("http://localhost:3000/", true)
                )

                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )

                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable());

        return http.build();
    }

    private boolean isDev() {
        return activeProfile.equals("dev") || activeProfile.equals("local");
    }
}
