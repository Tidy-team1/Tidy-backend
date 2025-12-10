package com.tidy.tidy.domain.comment;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.feedback.Feedback;
import com.tidy.tidy.domain.slide.Slide;
import com.tidy.tidy.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id", nullable = false)
    private Slide slide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String content;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentFeedback> commentFeedbacks = new ArrayList<>();

    @Builder
    public Comment(Slide slide, User user, String content) {
        this.slide = slide;
        this.user = user;
        this.content = content;
    }

    public void addCommentFeedback(CommentFeedback cf) {
        this.commentFeedbacks.add(cf);
    }


    public void updateContent(String content) {
        this.content = content;
    }

}
