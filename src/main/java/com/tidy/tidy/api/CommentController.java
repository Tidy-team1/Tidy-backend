package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.comment.CommentCreateRequest;
import com.tidy.tidy.api.dto.comment.CommentResponse;
import com.tidy.tidy.api.dto.comment.CommentUpdateRequest;
import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slides/{slideId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(
            @PathVariable Long slideId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CommentCreateRequest req) {

        return commentService.createComment(slideId, user.getId(), req);
    }

    @GetMapping
    public List<CommentResponse> getComments(@PathVariable Long slideId) {
        return commentService.getCommentsOfSlide(slideId);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CommentUpdateRequest req) {

        Long userId = user.getUser().getId();
        return commentService.updateComment(commentId, userId, req);
    }


    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        commentService.deleteComment(commentId, user.getUser().getId());
    }

    // 댓글에 피드백 연결
    @PostMapping("/{commentId}/feedbacks/{feedbackId}")
    public void addFeedbackToComment(
            @PathVariable Long commentId,
            @PathVariable Long feedbackId) {

        commentService.addFeedbackToComment(commentId, feedbackId);
    }

    // 댓글에서 피드백 제거
    @DeleteMapping("/{commentId}/feedbacks/{feedbackId}")
    public void removeFeedbackFromComment(
            @PathVariable Long commentId,
            @PathVariable Long feedbackId) {

        commentService.removeFeedbackFromComment(commentId, feedbackId);
    }
}
