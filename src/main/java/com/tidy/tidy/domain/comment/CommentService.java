package com.tidy.tidy.domain.comment;

import com.amazonaws.services.kms.model.NotFoundException;
import com.tidy.tidy.api.dto.comment.CommentCreateRequest;
import com.tidy.tidy.api.dto.comment.CommentResponse;
import com.tidy.tidy.api.dto.comment.CommentUpdateRequest;
import com.tidy.tidy.domain.feedback.Feedback;
import com.tidy.tidy.domain.feedback.FeedbackRepository;
import com.tidy.tidy.domain.slide.Slide;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import com.tidy.tidy.global.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final SlideRepository slideRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final CommentRepository commentRepository;
    private final CommentFeedbackRepository commentFeedbackRepository;

    /**
     * 댓글 생성
     */
    public CommentResponse createComment(Long slideId, Long userId, CommentCreateRequest req) {

        Slide slide = slideRepository.findById(slideId)
                .orElseThrow(() -> new NotFoundException("Slide not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Comment comment = Comment.builder()
                .slide(slide)
                .user(user)
                .content(req.getContent())
                .build();

        commentRepository.save(comment);

        // feedbackIds가 있는 경우 연결
        if (req.getFeedbackIds() != null) {
            for (Long feedbackId : req.getFeedbackIds()) {
                Feedback feedback = feedbackRepository.findById(feedbackId)
                        .orElseThrow(() -> new NotFoundException("Feedback not found"));

                CommentFeedback cf = new CommentFeedback(comment, feedback);
                commentFeedbackRepository.save(cf);

                comment.addCommentFeedback(cf);
            }
        }

        return toResponse(comment);
    }

    /**
     * 댓글 수정
     */
    public CommentResponse updateComment(Long commentId, Long userId, CommentUpdateRequest req) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        // 🔥 작성자 확인 로직 추가
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(req.getContent());
        return toResponse(comment);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long commentId, Long userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        // 🔥 작성자 확인 로직
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentFeedbackRepository.deleteAllByComment(comment);

        commentRepository.delete(comment);
    }

    /**
     * 댓글에 피드백 연결
     */
    public void addFeedbackToComment(Long commentId, Long feedbackId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found"));

        boolean exists = commentFeedbackRepository.existsByCommentAndFeedback(comment, feedback);
        if (!exists) {
            commentFeedbackRepository.save(new CommentFeedback(comment, feedback));
        }
    }

    /**
     * 댓글에서 피드백 제거
     */
    public void removeFeedbackFromComment(Long commentId, Long feedbackId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found"));

        commentFeedbackRepository.deleteByCommentAndFeedback(comment, feedback);
    }

    /**
     * 슬라이드의 모든 댓글 가져오기
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsOfSlide(Long slideId) {

        List<Comment> comments = commentRepository.findAllBySlideId(slideId);

        return comments.stream()
                .map(this::toResponse)
                .toList();
    }

    private CommentResponse toResponse(Comment comment) {

        List<Long> feedbackIds = comment.getCommentFeedbacks().stream()
                .map(cf -> cf.getFeedback().getId())
                .toList();

        return CommentResponse.builder()
                .id(comment.getId())
                .slideId(comment.getSlide().getId())

                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .userProfileImage(comment.getUser().getProfileImage())

                .content(comment.getContent())
                .feedbackIds(feedbackIds)

                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsOfPresentation(Long presentationId) {

        List<Long> slideIds = slideRepository.findIdsByPresentationId(presentationId);
        if (slideIds.isEmpty()) return List.of();

        List<Comment> comments =
                commentRepository.findAllBySlideIdInOrderByCreatedAtAsc(slideIds);

        return comments.stream()
                .map(this::toResponse)
                .toList();
    }
}
