package com.tidy.tidy.web;

import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.space.team.*;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.web.dto.PresentationResponse;
import com.tidy.tidy.web.dto.SpaceDetailResponse;
import com.tidy.tidy.web.dto.SpaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces")
public class SpaceController {

    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PresentationRepository presentationRepository;
    private final PresentationService presentationService;

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

    @PostMapping("/{spaceId}/presentations")
    public ResponseEntity<PresentationResponse> uploadPresentation(
            @PathVariable Long spaceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        // 1) 인증 사용자 꺼내기
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        User uploader = principal.getUser();

        // 2) Presentation 저장
        Presentation presentation = presentationService.savePresentation(spaceId, file, uploader);

        // 3) 응답
        return ResponseEntity.ok(new PresentationResponse(presentation));
    }
}
