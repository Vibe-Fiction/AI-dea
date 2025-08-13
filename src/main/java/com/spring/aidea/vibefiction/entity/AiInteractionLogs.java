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

    /**
     * 현재 AI 상호작용 로그를, 이를 기반으로 생성된 '이어쓰기 제안(Proposal)'과 연결합니다.
     * <p>
     * <b>[비즈니스 로직]</b> 사용자가 AI의 추천 내용을 채택하여 실제 제안으로 등록하는 경우,
     * 어떤 AI 로그에서 해당 제안이 비롯되었는지 출처를 추적할 수 있도록 양방향 연관관계를 설정하는 역할을 합니다.
     * <p>
     * 이 메서드는 주로 연관관계 편의 메서드(Convenience Method)로 사용되며,
     * 트랜잭션 내에서 호출되어야 영속성 컨텍스트에 변경 사항이 반영(dirty checking)됩니다.
     *
     * @param proposal 이 AI 상호작용 로그와 연결할 {@link Proposals} 엔티티.
     *                 이 값은 {@code null}이 아니어야 합니다.
     * @author 왕택준
     * @since 2025.08
     */
    public void setRelatedProposal(Proposals proposal) {
        this.relatedProposal = proposal;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
