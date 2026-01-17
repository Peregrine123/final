package com.jiyu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(
        name = "movie_comment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
@ToString(exclude = {"user", "movie"})
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class MovieComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    /**
     * Rating in [1,5]. Nullable to support "only comment" posts.
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * Short review content. Nullable to support "only rating" posts.
     *
     * Note: we still enforce max-length in service; column length is a soft DB guard.
     */
    @Column(name = "content", length = 512)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MovieCommentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            // No-audit mode: publish immediately.
            status = MovieCommentStatus.APPROVED;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
