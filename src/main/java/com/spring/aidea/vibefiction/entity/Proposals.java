package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proposals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"chapter", "proposer", "votes", "adoptedChapter"})
@Comment("이어쓰기 제안 테이블")
public class Proposals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long proposalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapters chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id", nullable = false)
    private Users proposer;

    @Column(name = "title", nullable = false, length = 60)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "vote_deadline", nullable = false)
    private LocalDateTime voteDeadline;

    @Column(name = "vote_count", nullable = false)
    private Integer voteCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "ai_generated", nullable = false)
    private Boolean aiGenerated;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- 연관관계 ---
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Votes> votes = new ArrayList<>();

    @OneToOne(mappedBy = "fromProposal")
    private Chapters adoptedChapter;

    // --- Enum ---
    public enum Status { VOTING, ADOPTED, REJECTED, DELETED }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = Status.VOTING;
        if (this.voteCount == null) this.voteCount = 0;
        if (this.aiGenerated == null) this.aiGenerated = false;
    }

    // TODO: 연관관계 매핑 (Novels, Chapters, Users, Votes)
}