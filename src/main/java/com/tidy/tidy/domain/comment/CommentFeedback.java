package com.tidy.tidy.domain.comment;

import com.tidy.tidy.domain.feedback.Feedback;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "comment_feedback",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_comment_feedback_unique",
                        columnNames = {"comment_id", "feedback_id"}
                )
        }
)
public class CommentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Comment 1 ---- N CommentFeedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    // Feedback 1 ---- N CommentFeedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    public CommentFeedback(Comment comment, Feedback feedback) {
        this.comment = comment;
        this.feedback = feedback;
    }
}
