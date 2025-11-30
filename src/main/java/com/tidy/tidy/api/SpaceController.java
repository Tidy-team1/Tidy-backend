package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.*;
import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.domain.slide.SlideService;
import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.space.team.*;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SpaceController {

    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PresentationRepository presentationRepository;
    private final PresentationService presentationService;
    private final SlideService slideService;
    private final UserRepository userRepository;

    // ---------------------------------------------
    // 1) TeamSpace 생성
    // ---------------------------------------------
    @PostMapping("/team")
    public ResponseEntity<?> createTeamSpace(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody Map<String, String> request
    ) {
        String name = request.get("name");
        User user = principal.getUser();

        // TeamSpace 생성
        TeamSpace teamSpace = TeamSpace.create(name);
        teamSpaceRepository.save(teamSpace);

        // Owner TeamMember 생성
        TeamMember ownerMember = TeamMember.create(user, teamSpace, TeamRole.OWNER);
        teamMemberRepository.save(ownerMember);

        return ResponseEntity.ok(Map.of(
                "id", teamSpace.getId(),
                "name", teamSpace.getName(),
                "type", teamSpace.getType().name()
        ));
    }
    // ---------------------------------------------
    // 2) 내 모든 Space 목록 조회 (PersonalSpace + TeamSpace)
    // ---------------------------------------------
    @GetMapping
    public ResponseEntity<?> getMySpaces(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        User user = principal.getUser();

        // 개인 스페이스
        PersonalSpace ps = personalSpaceRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalStateException("개인 공간이 존재하지 않습니다."));

        SpaceResponse personalResponse = SpaceResponse.fromPersonal(ps);

        // 팀 스페이스들
        List<TeamMember> memberships = teamMemberRepository.findByUser(user);
        List<SpaceResponse> teams = memberships.stream()
                .map(m -> SpaceResponse.fromTeam(m.getTeamSpace()))
                .toList();

        // personal + team 목록 통합
        List<SpaceResponse> result = new java.util.ArrayList<>();
        result.add(personalResponse);
        result.addAll(teams);

        return ResponseEntity.ok(result);
    }


    // ---------------------------------------------
    // 3) 내 PersonalSpace 상세 조회
    // ---------------------------------------------
    @GetMapping("/personal")
    public ResponseEntity<?> getMyPersonalSpace(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        User user = principal.getUser();

        PersonalSpace ps = personalSpaceRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalStateException("개인 스페이스가 없습니다."));

        // presentation 조회
        List<PresentationResponse> presentations =
                presentationRepository.findBySpaceId(ps.getId()).stream()
                        .map(PresentationResponse::new)
                        .toList();

        return ResponseEntity.ok(
                SpaceDetailResponse.from(ps, null, presentations)
        );
    }


    // ---------------------------------------------
    // 4) 내 TeamSpace 목록 조회
    // ---------------------------------------------
    @GetMapping("/team")
    public ResponseEntity<?> getMyTeamSpaces(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        User user = principal.getUser();

        List<TeamMember> memberships = teamMemberRepository.findByUser(user);

        List<SpaceResponse> teams = memberships.stream()
                .map(m -> SpaceResponse.fromTeam(m.getTeamSpace()))
                .toList();

        return ResponseEntity.ok(teams);
    }

    // ---------------------------------------------
    // 5) Space 상세 조회 (ID 가지고 조회)
    // ---------------------------------------------
    @GetMapping("/{spaceId}")
    public ResponseEntity<?> getSpace(
            @PathVariable Long spaceId
    ) {
        // PersonalSpace 먼저 조회
        Space space = personalSpaceRepository.findById(spaceId)
                .map(s -> (Space) s)
                .orElse(null);

        if (space == null) {
            space = teamSpaceRepository.findById(spaceId)
                    .map(s -> (Space) s)
                    .orElseThrow(() -> new IllegalArgumentException("해당 스페이스가 존재하지 않습니다."));
        }

        // presentation 조회
        List<PresentationResponse> presentations =
                presentationRepository.findBySpaceId(spaceId).stream()
                        .map(PresentationResponse::new)
                        .toList();

        // TeamSpace
        if (space instanceof TeamSpace ts) {
            List<TeamMember> members = teamMemberRepository.findByTeamSpace(ts);
            return ResponseEntity.ok(
                    SpaceDetailResponse.from(ts, members, presentations)
            );
        }

        // PersonalSpace
        return ResponseEntity.ok(
                SpaceDetailResponse.from(space, null, presentations)
        );
    }

    // ---------------------------------------------
    // 6) 특정 Space 내 Presentation 목록 조회
    // ---------------------------------------------
    @GetMapping("/{spaceId}/presentations")
    public ResponseEntity<?> getPresentationsInSpace(
            @PathVariable Long spaceId
    ) {
        // Space 존재 여부 확인
        boolean exists =
                personalSpaceRepository.existsById(spaceId)
                        || teamSpaceRepository.existsById(spaceId);

        if (!exists) {
            throw new IllegalArgumentException("해당 스페이스가 존재하지 않습니다.");
        }

        // presentations 조회
        List<PresentationResponse> presentations = presentationRepository
                .findBySpaceId(spaceId)
                .stream()
                .map(PresentationResponse::new)
                .toList();

        return ResponseEntity.ok(presentations);
    }

    // ---------------------------------------------
    // 7) 특정 Space에 Presentaiton 업로드
    // ---------------------------------------------
    @PostMapping("/{spaceId}/presentations")
    public ResponseEntity<PresentationResponse> uploadPresentation(
            @PathVariable Long spaceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        User uploader = principal.getUser();

        // 1) Presentation 저장 (DB + original.pptx 저장)
        Presentation presentation = presentationService.savePresentation(spaceId, file, uploader);

        // 2) 썸네일 생성 (이 시점엔 savePresentation 트랜잭션 끝난 상태)
        slideService.generateThumbnails(presentation.getId());

        // 3) 응답
        return ResponseEntity.ok(new PresentationResponse(presentation));
    }
    // ---------------------------------------------
    // 8) TeamSpace 멤버 초대
    // ---------------------------------------------
    @PostMapping("/{spaceId}/members")
    public ResponseEntity<?> inviteTeamMember(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody MemberInviteRequest request
    ) {
        String email = request.getEmail();
        TeamRole role = TeamRole.valueOf(request.getRole());
        User inviter = principal.getUser();

        // 1) TeamSpace 존재 확인
        TeamSpace teamSpace = teamSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀 스페이스가 존재하지 않습니다."));

        // 2) 초대하는 사람이 OWNER인지 검사 (권한 체크)
        TeamMember inviterMember = teamMemberRepository.findByUserAndTeamSpace(inviter, teamSpace)
                .orElseThrow(() -> new IllegalArgumentException("스페이스 멤버가 아닙니다."));

        if (inviterMember.getRole() != TeamRole.OWNER) {
            throw new IllegalArgumentException("멤버를 초대할 권한이 없습니다.");
        }

        // 3) 초대할 사용자 조회
        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        // 4) 이미 멤버인지 검사
        if (teamMemberRepository.existsByUserAndTeamSpace(invitedUser, teamSpace)) {
            throw new IllegalArgumentException("이미 팀 스페이스의 멤버입니다.");
        }

        // 5) 멤버 생성
        TeamMember newMember = TeamMember.create(invitedUser, teamSpace, role);
        teamMemberRepository.save(newMember);

        // 6) 응답 생성
        Map<String, Object> response = Map.of(
                "spaceId", teamSpace.getId(),
                "invitedMember", Map.of(
                        "id", newMember.getId(),
                        "name", invitedUser.getName(),
                        "role", newMember.getRole().name()
                )
        );

        return ResponseEntity.ok(response);
    }


    // ---------------------------------------------
    // 9) 멤버 여러 명 초대 + TeamSpace 생성
    // ---------------------------------------------
    @PostMapping("/team-with-members")
    public ResponseEntity<?> createTeamSpaceWithMembers(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody CreateTeamSpaceWithMembersRequest request
    ) {
        String name = request.getName();
        List<MemberInviteRequest> members = request.getMembers();

        User owner = principal.getUser();

        // 1) TeamSpace 생성
        TeamSpace teamSpace = TeamSpace.create(name);
        teamSpaceRepository.save(teamSpace);

        // 2) Owner 팀멤버 생성
        TeamMember ownerMember = TeamMember.create(owner, teamSpace, TeamRole.OWNER);
        teamMemberRepository.save(ownerMember);

        // 응답 멤버 리스트
        List<Map<String, Object>> savedMembers = new java.util.ArrayList<>();

        // Owner 먼저 넣기
        savedMembers.add(Map.of(
                "id", owner.getId(),
                "name", owner.getName(),
                "role", "OWNER"
        ));

        // 3) members에 적힌 사용자들 초대
        if (members != null) {
            for (MemberInviteRequest m : members) {
                String email = m.getEmail();
                TeamRole role = TeamRole.valueOf(m.getRole());

                // 초대 대상 user 조회
                User invitedUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

                // 중복 멤버인지 확인
                if (!teamMemberRepository.existsByUserAndTeamSpace(invitedUser, teamSpace)) {
                    TeamMember newMember = TeamMember.create(invitedUser, teamSpace, role);
                    teamMemberRepository.save(newMember);

                    savedMembers.add(Map.of(
                            "id", invitedUser.getId(),
                            "name", invitedUser.getName(),
                            "role", newMember.getRole().name()
                    ));
                }
            }
        }

        // 4) 응답 생성
        Map<String, Object> response = Map.of(
                "spaceId", teamSpace.getId(),
                "members", savedMembers
        );

        return ResponseEntity.ok(response);
    }

}
