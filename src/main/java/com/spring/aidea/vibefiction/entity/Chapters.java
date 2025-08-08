package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Comment("소설 회차 테이블")
public class Chapters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long chapterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novels novel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_proposal_id")
    private Proposals fromProposal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- 연관관계 ---
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposals> proposals = new ArrayList<>();

    // --- Enum ---
    public enum Status { PUBLISHED, DELETED }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = Status.PUBLISHED;
    }

    // TODO: 연관관계 매핑 (Novels, Users, Proposals)
}