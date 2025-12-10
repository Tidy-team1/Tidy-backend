package com.tidy.tidy.domain.comment;

import com.tidy.tidy.domain.feedback.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentFeedbackRepository extends JpaRepository<CommentFeedback, Long> {

    boolean existsByCommentAndFeedback(Comment comment, Feedback feedback);

    void deleteAllByComment(Comment comment);

    void deleteByCommentAndFeedback(Comment comment, Feedback feedback);
}
