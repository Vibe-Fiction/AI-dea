package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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

    /*
     * [리팩토링-AS-IS] 기존 @Lob 어노테이션
     *
     * [주석 처리 이유] by 왕택준
     * Chapters 엔티티와 동일한 이유로, 모든 환경에서 일관된 스키마(MEDIUMTEXT)를 보장하고
     * 저장 용량을 명확하게 지정하기 위해 @Column(columnDefinition = "...") 옵션을 사용합니다.
     *
    @Lob
    @Column(name = "content", nullable = false)
    private String content;
    */

    /**
     * [리팩토링-TO-BE] 이어쓰기 제안의 본문 내용입니다.
     * {@code columnDefinition = "MEDIUMTEXT"} 설정을 통해, DB 컬럼 타입을 명시적으로 지정하여
     * 길이 제한(최대 5000자)을 넘어서는 긴 본문도 안정적으로 저장할 수 있습니다.
     */
    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
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

    public void incrementVoteCount() {
        this.voteCount += 1;
    }

    public void decrementVoteCount() {
        this.voteCount -= 1;
    }

    public void setVoteDeadline(LocalDateTime voteDeadline) {
    }

    // --- Enum ---
    public enum Status { VOTING, ADOPTED, REJECTED, DELETED, PENDING }
    /**
     * [리팩토링-TO-BE] enum의 속성 한개 더 추가할 예정
     */
    /**
     * 새로운 '이어쓰기 제안(Proposal)' 엔티티를 생성하고 초기화하는 정적 팩토리 메서드입니다.
     *
     * 이 메서드는 새로운 제안을 생성하는 데 필요한 초기 설정과 비즈니스 규칙을 캡슐화합니다.
     * 특히, 제안이 AI를 통해 생성되었는지 여부(aiGenerated)를 판단하고,
     * 제안의 상태를 '투표 진행 중(VOTING)'으로 초기화합니다. (TODO: 상태 변경 로직 추가)
     *
     * @param chapter  이 제안이 속하는 {@link Chapters} 엔티티.
     * @param proposer 이 제안을 작성한 사용자({@link Users}) 엔티티.
     * @param title    제안의 제목.
     * @param content  제안의 본문 내용.
     * @param aiLog    이 제안이 AI를 통해 생성되었을 경우, 관련 {@link AiInteractionLogs} 엔티티.
     *                 AI를 사용하지 않은 경우 {@code null}이 됩니다.
     * @return 필요한 모든 정보가 설정된 새로운 {@link Proposals} 인스턴스.
     *         (주의: 이 메서드는 객체를 생성만 할 뿐, 영속화(persist)하지는 않습니다.)
     * @author 왕택준
     * @since 2025.08
     */
    public static Proposals create(Chapters chapter, Users proposer, String title, String content, AiInteractionLogs aiLog) {
        // [비즈니스 규칙] AI 사용 여부에 따라 aiGenerated 플래그 설정
        boolean aiGenerated = (aiLog != null);

        // [TODO(#88): 제안 생성 시 상태를 'VOTING'으로 초기화하도록 변경]
        return Proposals.builder()
            .chapter(chapter)
            .proposer(proposer)
            .title(title)
            .content(content)
            .aiGenerated(aiGenerated)
            .voteDeadline(chapter.getCreatedAt() // <- Chapters 엔티티의 생성일을 가져와서
                // 테스트 용으로 1분 뒤로 생성
                /*.plusDays(0)
                .withHour(0)
                .withMinute(1)
                .withSecond(0)*/
                .plusMinutes(1)
                .withNano(0) // 마감일 계산 로직을 통합) // create 메서드에서 초기화
            )
            .build();
    }

    /**
     * [JPA 생명주기 콜백]
     * 엔티티가 영속화되기 직전에 호출되어 기본값을 설정합니다.
     * 기존 onCreate() 메서드를 확장하여 다른 팀원과의 호환성을 유지합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // "작성일 기준 3일 뒤 마감" 정책을 반영합니다.

        if (this.status == null) {
            this.status = Status.VOTING;
        }
        if (this.voteCount == null) {
            this.voteCount = 0;
        }
        if (this.aiGenerated == null) {
            this.aiGenerated = false;
        }
    }

    // --- 비즈니스 로직 메서드 ---

    /**
     * 제안의 상태를 변경합니다.
     * @param status 변경할 새로운 상태.
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
