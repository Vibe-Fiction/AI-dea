package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_interaction_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "basedOnChapter", "relatedProposal"})
@Comment("AI 상호작용 기록 테이블")
public class AiInteractionLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", columnDefinition = "BIGINT")
    @Comment("로그 ID")
    private Long logId;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("AI 호출 목적")
    private AiInteractionType type;


    @Lob
    @Column(name = "prompt")
    @Comment("사용자 입력 프롬프트")
    private String prompt;

    @Lob
    @Column(name = "result")
    @Comment("AI 생성 결과")
    private String result;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    // TODO: 연관관계 매핑 (Users, Chapters, Proposals)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "based_on_chapter_id")
    private Chapters basedOnChapter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_proposal_id")
    private Proposals relatedProposal;



    public enum AiInteractionType {
        NOVEL_CREATION,
        PROPOSAL_GENERATION
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}