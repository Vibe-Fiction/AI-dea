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
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "novels")
@Getter
@EqualsAndHashCode(of = "novelId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"author", "novelGenres", "collaborators", "chapters", "favorites"})
@Comment("소설 테이블")
public class Novels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id", columnDefinition = "BIGINT")
    @Comment("소설 ID")
    private Long novelId;


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
    @Builder.Default
    @OrderBy("novel ASC")
    // private List<NovelGenres> novelGenres = new ArrayList<>();
    // [설계 의도] List를 Set으로 변경
    // '한 소설은 동일한 장르를 중복해서 가질 수 없다'는 비즈니스 규칙과
    // 데이터베이스의 UNIQUE 제약 조건을 애플리케이션 자료구조 수준에서도 보장하기 위함입니다.
    private Set<NovelGenres> novelGenres = new HashSet<>(); // <- 변경 제안

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Collaborators> collaborators = new HashSet<>();


    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Favorites> favorites = new HashSet<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("chapterNumber ASC")
    private List<Chapters> chapters = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @Comment("소설 원작자 (Users 참조)")
    private Users author;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastUpdatedAt = now;
        if (this.visibility == null) this.visibility = visibility.PUBLIC;
        if (this.status == null) this.status = status.ONGOING;
        if (this.viewCount == null) this.viewCount = 0L;
        if (this.coverImageUrl == null || this.coverImageUrl.isBlank()) {
            this.coverImageUrl = "/img/Relai-logo-400X550.png";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();


    }

    /**
     * 새로운 소설 엔티티를 생성하고, 연관된 장르들을 한 번에 연결하는 정적 팩토리 메서드입니다.
     *
     * 이 메서드는 복잡한 소설 생성 로직을 서비스 레이어에서 도메인 모델 자체로 옮겨와,
     * 도메인의 응집도를 높이고 비즈니스 로직을 중앙에서 관리하도록 돕습니다.
     * 내부적으로는 전달받은 각 {@code Genres} 엔티티에 대해 {@link NovelGenres} 매핑 엔티티를 생성하여
     * 다대다(N:M) 관계를 설정합니다.
     *
     * @param author     소설을 집필한 원작자({@link Users}) 엔티티.
     * @param title      생성할 소설의 제목.
     * @param synopsis   소설의 시놉시스 (선택 사항).
     * @param visibility 소설의 공개 범위({@link NovelVisibility}).
     * @param genres     소설에 부여할 {@link Genres} 엔티티의 목록.
     * @return 모든 초기 정보와 장르 연관관계가 설정된 새로운 {@link Novels} 인스턴스.
     *         (주의: 이 메서드는 객체를 생성만 할 뿐, 영속화(persist)하지는 않습니다.)
     * @author 왕택준
     * @since 2025.08
     */
    public static Novels create(Users author, String title, String synopsis, NovelVisibility visibility, List<Genres> genres) {
        Novels novel = Novels.builder()
            .author(author)
            .title(title)
            .synopsis(synopsis)
            .visibility(visibility)
            .build();

        // [비즈니스 로직] 제공된 장르 목록을 기반으로 NovelGenres 매핑 엔티티를 생성하여 연결
        if (genres != null) {
            novel.novelGenres = genres.stream()
                .map(genre -> NovelGenres.create(novel, genre))
                .collect(Collectors.toSet());
        }
        return novel;
    }

    /**
     * 소설에 새로운 회차({@link Chapters})를 추가하고, 양방향 연관관계의 양쪽을 모두 설정하는 연관관계 편의 메서드입니다.
     *
     * <b>[설계 의도]</b>
     * 연관관계의 주인이 아닌 {@code Novels} 엔티티에서 이 메서드를 제공함으로써, 개발자가 실수로
     * 한쪽의 연관관계만 설정하는 것을 방지하고(객체 상태의 불일치 예방), 항상 양쪽 모두가
     * 일관성 있게 유지되도록 보장합니다. 즉, {@code novel.getChapters().add(chapter)}와
     * {@code chapter.setNovel(novel)}을 원자적으로 처리합니다.
     *
     * @param chapter 이 소설에 추가할 새로운 {@link Chapters} 엔티티.
     * @author 왕택준
     * @since 2025.08
     */
    public void addChapter(Chapters chapter) {
        this.chapters.add(chapter);
        chapter.setNovel(this);
    }

}
