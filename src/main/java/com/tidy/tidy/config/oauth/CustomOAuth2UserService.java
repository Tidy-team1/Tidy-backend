// com.tidy.tidy.config.oauth.CustomOAuth2UserService
package com.tidy.tidy.config.oauth;

import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.user.Provider;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PersonalSpaceRepository personalSpaceRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);
        String registrationId = req.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (!"google".equals(registrationId)) {
            throw new IllegalArgumentException("지원하지 않는 provider: " + registrationId);
        }

        // 구글 OAuth 정보
        String sub = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");

        // 유저 저장 또는 업데이트
        AtomicBoolean isNewUser = new AtomicBoolean(false);

        User user = userRepository.findByProviderAndProviderId(Provider.GOOGLE, sub)
                .map(u -> {
                    u.changeProfileImage(picture);
                    return u;
                })
                .orElseGet(() -> {
                    isNewUser.set(true);

                    return userRepository.save(
                            User.builder()
                                    .name(name)
                                    .email(email)
                                    .profileImage(picture)
                                    .provider(Provider.GOOGLE)
                                    .providerId(sub)
                                    .build()
                    );
                });

        // ⭐ 신규 사용자일 때 PersonalSpace 자동 생성
        if (isNewUser.get()) {
            String spaceName = name + "님의 개인 스페이스";

            PersonalSpace personalSpace = PersonalSpace.create(spaceName, user);
            personalSpaceRepository.save(personalSpace);
        }

        return new CustomOAuth2User(user, attributes);
    }
}
