package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자와 AI 모델 간의 모든 상호작용을 기록하는 엔티티입니다.
 * <p>
 * 이 로그는 향후 서비스 사용 패턴 분석, 비용 추적, 문제 해결 및
 * AI 모델 성능 개선을 위한 데이터로 활용될 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Entity
@Table(name = "ai_interaction_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "basedOnChapter", "relatedProposal"})
@Comment("AI 상호작용 기록 테이블")
public class AiInteractionLogs {

    /**
     * AI 상호작용 로그의 고유 식별자(Primary Key)입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    @Comment("로그 ID")
    private Long logId;

    /**
     * 이 AI 상호작용을 요청한 사용자입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("AI를 사용한 사용자 ID")
    private Users user;

    /**
     * AI 호출의 목적을 나타내는 타입입니다. (예: 새 소설 생성, 이어쓰기 제안)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("AI 호출 목적")
    private AiInteractionType type;


    /*
     * [리팩토링-AS-IS] 기존 @Lob 어노테이션
     *
     * [주석 처리 이유] by 왕택준
     * @Lob 어노테이션은 데이터베이스 종류에 따라 매핑되는 실제 타입(TEXT, LONGTEXT 등)이 달라질 수 있습니다.
     * 모든 개발 및 운영 환경에서 일관된 스키마를 보장하고, 저장 용량을 명확하게(MEDIUMTEXT: 약 16MB)
     * 지정하기 위해 @Column(columnDefinition = "...") 옵션을 사용하는 방식으로 변경합니다.
     *
    @Lob
    @Column(name = "prompt")
    private String prompt;

    @Lob
    @Column(name = "result")
    private String result;
    */

    /**
     * [리팩토링-TO-BE] AI에게 전달된 전체 프롬프트 문자열입니다.
     * {@code columnDefinition = "MEDIUMTEXT"} 설정을 통해, DB 컬럼 타입을 명시적으로 지정하여
     * 길이 제한 없이 긴 프롬프트를 안정적으로 저장할 수 있습니다.
     */
    @Column(name = "prompt", columnDefinition = "MEDIUMTEXT")
    @Comment("사용자 입력 프롬프트")
    private String prompt;

    /**
     * [리팩토링-TO-BE] AI로부터 받은 원본 결과 문자열입니다.
     * {@code columnDefinition = "MEDIUMTEXT"} 설정을 통해 긴 생성 결과를 그대로 저장할 수 있습니다.
     */
    @Column(name = "result", columnDefinition = "MEDIUMTEXT")
    @Comment("AI 생성 결과")
    private String result;

    /**
     * 이어쓰기 제안 생성 시, AI가 참고한 기반 회차 정보입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "based_on_chapter_id")
    @Comment("이어쓰기 제안 시 기반이 된 회차 ID")
    private Chapters basedOnChapter;

    /**
     * 이 AI 상호작용을 통해 최종적으로 등록된 이어쓰기 제안 정보입니다.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_proposal_id")
    @Comment("이 로그를 통해 최종 등록된 제안의 ID")
    private Proposals relatedProposal;

    /**
     * 이 로그가 생성된 일시입니다.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    /**
     * AI 상호작용의 목적을 나타내는 Enum 타입입니다.
     */
    public enum AiInteractionType {
        NOVEL_CREATION,
        PROPOSAL_GENERATION
    }

    /**
     * 현재 AI 상호작용 로그에 연결된 제안({@link Proposals})을 설정하는 메서드입니다.
     * <p>
     * AI 추천을 통해 사용자가 제안을 최종 등록할 때, 이 메서드를 호출하여
     * 어떤 AI 로그가 어떤 제안으로 이어졌는지 관계를 맺어줍니다.
     *
     * @param proposal 이 로그와 연결할 {@link Proposals} 엔티티.
     */
    public void setRelatedProposal(Proposals proposal) {
        this.relatedProposal = proposal;
    }

    /**
     * JPA 엔티티가 영속화되기 직전에 호출되는 생명주기 콜백 메서드입니다.
     * {@code @CreationTimestamp}가 동작하지 않는 특정 상황을 대비하여 생성 시간을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
