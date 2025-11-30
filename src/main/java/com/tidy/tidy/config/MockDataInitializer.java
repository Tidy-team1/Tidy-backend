package com.tidy.tidy.config;

import com.tidy.tidy.domain.user.Provider;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "dev"})   // 운영에서는 절대 실행되지 않게
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PersonalSpaceRepository personalSpaceRepository;

    @Override
    public void run(String... args) {

        // 이미 존재하면 스킵
        if (userRepository.count() > 0) {
            return;
        }

        // ---------------------------
        // 1) Mock User 생성
        // ---------------------------
        User owner = User.builder()
                .email("owner@tidy.com")
                .name("오너유저")
                .profileImage("https://example.com/user1.png")
                .provider(Provider.LOCAL)               // 필수 필드
                .providerId("mock-owner-001")   // ⭐ 필수 providerId
                .build();

        User member1 = User.builder()
                .email("member1@tidy.com")
                .name("지연")
                .profileImage("https://example.com/user2.png")
                .provider(Provider.LOCAL)
                .providerId("mock-member-001")  // ⭐
                .build();

        User member2 = User.builder()
                .email("member2@tidy.com")
                .name("경민")
                .profileImage("https://example.com/user3.png")
                .provider(Provider.LOCAL)
                .providerId("mock-member-002")  // ⭐
                .build();

        userRepository.save(owner);
        userRepository.save(member1);
        userRepository.save(member2);

        // ---------------------------
        // 2) 각 유저의 PersonalSpace 생성
        // ---------------------------
        createPersonalSpace(owner, "오너의 개인 스페이스");
        createPersonalSpace(member1, "지연의 개인 스페이스");
        createPersonalSpace(member2, "경민의 개인 스페이스");

        System.out.println("🎉 Mock User + PersonalSpace 생성 완료!");
    }

    private void createPersonalSpace(User user, String spaceName) {
        PersonalSpace ps = PersonalSpace.create(spaceName, user); // ⭐ name 문자열 필요
        personalSpaceRepository.save(ps);
    }
}
