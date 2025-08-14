package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "novel_genres",uniqueConstraints = @UniqueConstraint(columnNames = {"novel_id", "genre_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"novel", "genre"}) // 무한 재귀 방지를 위해 연관관계 필드 제외
@EqualsAndHashCode(exclude = {"novel", "genre"}) // 무한 재귀 방지를 위해 연관관계 필드 제외
@Comment("소설-장르 N:M 연결 테이블")
public class NovelGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_genres_id", columnDefinition = "BIGINT")
    private Long id;


    // 소설 ID (복합 키의 일부)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    @Comment("소설 ID")
    private Novels novel;

    // 장르 ID (복합 키의 일부)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    @Comment("장르 ID")
    private Genres genre;

    /**
     * 소설(Novel)과 장르(Genre) 사이의 다대다(N:M) 관계를 연결하는
     * {@link NovelGenres} 매핑 엔티티를 생성하는 정적 팩토리 메서드입니다.
     *
     * 이 메서드는 특정 소설에 특정 장르를 할당하는 연관관계를 설정하기 위해 사용됩니다.
     * 생성된 {@code NovelGenres} 엔티티는 소설과 장르의 중간 다리 역할을 합니다.
     * <p>
     * <b>[사용 시나리오]</b>
     * 사용자가 소설을 생성하거나 수정할 때, 선택한 여러 장르 각각에 대해 이 메서드를 호출하여
     * 소설과 장르를 연결하는 매핑 엔티티들을 생성합니다.
     *
     * @param novel 연결할 대상이 되는 {@link Novels} 엔티티.
     * @param genre 소설에 부여할 {@link Genres} 엔티티.
     * @return 소설과 장르가 연결된 새로운 {@link NovelGenres} 인스턴스.
     *         (주의: 이 메서드는 객체를 생성만 할 뿐, 영속화하지는 않습니다.)
     * @author 왕택준
     * @since 2025.08
     */
    public static NovelGenres create(Novels novel, Genres genre) {
        return NovelGenres.builder()
            .novel(novel)
            .genre(genre)
            .build();
    }

    // TODO: 연관관계 편의 메서드 추가 가능
}
