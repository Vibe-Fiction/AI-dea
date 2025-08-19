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
@ToString(exclude = {"novel", "author", "fromProposal", "proposals"})
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

    /*
     * [리팩토링-AS-IS] 기존 @Lob 어노테이션
     *
     * [주석 처리 이유] by 왕택준
     * @Lob 어노테이션은 데이터베이스 종류에 따라 매핑되는 실제 타입(TEXT, LONGTEXT 등)이 달라질 수 있습니다.
     * 모든 개발 및 운영 환경에서 일관된 스키마(MEDIUMTEXT)를 보장하기 위해
     * @Column(columnDefinition = "...") 옵션을 사용하여 타입을 명시적으로 지정하는 방식으로 변경합니다.
     *
    @Lob
    @Column(name = "content", nullable = false)
    private String content;
    */

    /**
     * [리팩토링-TO-BE] 회차의 본문 내용입니다.
     * {@code columnDefinition = "MEDIUMTEXT"} 설정을 통해, DB 컬럼 타입을 명시적으로 지정하여
     * 길이 제한(최대 5000자)을 넘어서는 긴 본문도 안정적으로 저장할 수 있습니다.
     */
    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
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
    @Builder.Default
    private List<Proposals> proposals = new ArrayList<>();

    // --- Enum ---
    public enum Status { PUBLISHED, DELETED }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = Status.PUBLISHED;
    }

    /**
     * 새로운 회차(Chapter) 엔티티를 생성하고 초기화하는 정적 팩토리 메서드입니다.
     *
     * 이 메서드는 회차 생성에 필요한 복잡한 초기화 로직을 캡슐화합니다. 특히, 회차 번호는
     * 부모 소설({@code novel})에 이미 등록된 회차의 개수를 기반으로 자동으로 설정하여 일관성을 보장합니다.
     * <p>
     * <b>[주요 로직]</b>
     *  회차 번호는 {@code novel.getChapters().size() + 1}로 계산됩니다. 이 방식은 부모 소설이
     *  아직 영속화되지 않은 상태(ID가 없는 상태)에서도 안전하게 동작합니다.
     *
     * @param novel        이 회차가 종속될 부모 {@link Novels} 엔티티.
     * @param author       이 회차를 작성한 사용자({@link Users}) 엔티티.
     * @param title        생성할 회차의 제목.
     * @param content      생성할 회차의 본문 내용.
     * @param fromProposal 이 회차가 특정 '이어쓰기 제안'을 채택하여 생성된 경우, 그 원본이 되는
     *                     {@link Proposals} 엔티티입니다. 제안 없이 직접 작성된 경우 {@code null}이 될 수 있습니다.
     * @return 필요한 모든 정보가 설정된 새로운 {@link Chapters} 인스턴스.
     *         (주의: 이 메서드는 객체를 생성만 할 뿐, 영속화하지는 않습니다.)
     * @author 왕택준
     * @since 2025.08
     */
    public static Chapters create(Novels novel, Users author, String title, String content, Proposals fromProposal) {
        // [비즈니스 규칙] 회차 번호는 항상 기존 회차 수 + 1로 자동 부여
        int chapterNumber = novel.getChapters().size() + 1;

        return Chapters.builder()
            .novel(novel)
            .author(author)
            .chapterNumber(chapterNumber)
            .title(title)
            .content(content)
            .fromProposal(fromProposal)
            .build();
    }

    /**
     * 양방향 연관관계의 편의성을 위해 사용되는 내부 setter입니다. (package-private)
     * <p>
     * <b>[설계 의도]</b>
     * 이 메서드의 접근 제어자를 package-private으로 설정한 이유는,
     * {@link Chapters}와 {@link Novels} 간의 연관관계 설정은 반드시 부모 엔티티인
     * {@code Novels}의 연관관계 편의 메서드(예: {@code addChapter})를 통해서만 이루어지도록 강제하기 위함입니다.
     * 이를 통해 연관관계의 무결성을 보장하고, 외부에서의 직접적인 상태 변경을 방지합니다.
     *
     * @param novel 연결할 부모 {@link Novels} 엔티티.
     */
    void setNovel(Novels novel) {
        this.novel = novel;
    }

    // TODO: 연관관계 매핑 (Novels, Users, Proposals)
}
