// com.tidy.tidy.config.oauth.CustomOAuth2UserService
package com.tidy.tidy.config.oauth;

import com.tidy.tidy.domain.user.Provider;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);
        String registrationId = req.getClientRegistration().getRegistrationId(); // "google"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (!"google".equals(registrationId)) {
            // 필요시 네이버/카카오 등 추가 분기
            throw new IllegalArgumentException("지원하지 않는 provider: " + registrationId);
        }

        // 구글 표준 OIDC 필드
        String sub = (String) attributes.get("sub"); // providerId
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");

        // 저장 또는 갱신
        User user = userRepository.findByProviderAndProviderId(Provider.GOOGLE, sub)
                .map(u -> {
                    // 업데이트 필드(표시용 데이터) 갱신
                    u.changeProfileImage(picture);
                    // 이름 정책에 따라 업데이트할지 결정
                    // u.changeName(name);
                    return u;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(name)
                                .email(email)
                                .profileImage(picture)
                                .provider(Provider.GOOGLE)
                                .providerId(sub)
                                .build()
                ));

        // 권한은 일단 ROLE_USER 하나 부여 (Security 6: SimpleGrantedAuthority)
        return new DefaultOAuth2User(
                Set.of(() -> "ROLE_USER"),
                attributes,
                "sub" // nameAttributeKey: 고유키
        );
    }
}
