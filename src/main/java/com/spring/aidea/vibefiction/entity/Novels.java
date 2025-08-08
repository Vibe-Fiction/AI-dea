package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Entity
@Table(name = "novels")
@Getter
@EqualsAndHashCode(of = "novelId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Comment("소설 테이블")
public class Novels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id", columnDefinition = "BIGINT")
    @Comment("소설 ID")
    private Long novelId;

    @Column(name = "author_id", nullable = false)
    @Comment("소설 원작자 ID")
    private Long authorId;

    @Column(name = "title", length = 50, nullable = false)
    @Comment("소설 제목")
    private String title;

    @Lob
    @Column(name = "synopsis")
    @Comment("소설 시놉시스(줄거리)")
    private String synopsis;

    @Column(name = "cover_image_url", length = 255)
    @Comment("소설 표지 이미지 경로")
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @Comment("공개 범위")
    private NovelVisibility visibility;

    @Column(name = "access_password", length = 255)
    @Comment("비공개/친구공개 시 접근 비밀번호 (암호화)")
    private String accessPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("연재 상태")
    private NovelStatus status;

    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    private Long viewCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    @Comment("최종 수정일시")
    private LocalDateTime lastUpdatedAt;

    public enum NovelStatus {
        ONGOING,
        AWAITING_AUTHOR,
        COMPLETED,
        HIDDEN,
        DELETED
    }

    public enum NovelVisibility {
        PUBLIC,
        PRIVATE,
        FRIENDS
    }


    // TODO: 연관관계 매핑 (Users, NovelGenres, Collaborators, Chapters)
    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelGenres> novelGenres = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Collaborators> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapters> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorites> favorites = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastUpdatedAt = now;
        if (this.visibility == null) this.visibility = visibility.PUBLIC;
        if (this.status == null) this.status = status.ONGOING;
        if (this.viewCount == null) this.viewCount = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();


    }
}