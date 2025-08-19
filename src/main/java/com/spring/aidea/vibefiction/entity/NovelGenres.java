package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.Objects;

/**
 * 소설({@link Novels})과 장르({@link Genres}) 간의 다대다(N:M) 관계를 매핑하는 연결 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 JPA에서 다대다 관계를 표현하는 표준적인 방법 중 하나인,
 * 중간 테이블을 별도의 엔티티로 직접 모델링하는 방식을 따릅니다.
 *
 * @author 왕택준 (Original Author inferred from context)
 * @since 2025.08
 */
@Entity
@Table(name = "novel_genres",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_NovelGenres_NovelGenre", // 제약조건에 이름을 부여하여 관리 용이성 향상
        columnNames = {"novel_id", "genre_id"}
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"novel", "genre"}) // 양방향 연관관계에서 무한 순환 참조를 방지
public class NovelGenres {

    /**
     * 연결 관계 자체의 고유 식별자(Primary Key)입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_genres_id")
    private Long id;

    /**
     * 이 관계에 속한 소설 엔티티입니다.
     * <p>
     * {@code @ManyToOne} 관계이며, {@code novel_id} 컬럼을 통해 {@link Novels} 테이블과 연결됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novels novel;

    /**
     * 이 관계에 속한 장르 엔티티입니다.
     * <p>
     * {@code @ManyToOne} 관계이며, {@code genre_id} 컬럼을 통해 {@link Genres} 테이블과 연결됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genres genre;

    /**
     * {@link Novels}와 {@link Genres} 객체를 받아 새로운 {@code NovelGenres} 인스턴스를 생성하는 정적 팩토리 메서드입니다.
     * <p>
     * {@code @Builder}를 직접 사용하는 것보다, 생성의 의도를 명확하게 표현할 수 있습니다.
     *
     * @param novel 연결할 소설 엔티티
     * @param genre 연결할 장르 엔티티
     * @return 생성된 {@code NovelGenres} 인스턴스 (아직 영속화되지 않은 상태)
     */
    public static NovelGenres create(Novels novel, Genres genre) {
        return NovelGenres.builder()
            .novel(novel)
            .genre(genre)
            .build();
    }

    /**
     * 객체의 동등성을 비교하기 위해 {@code equals} 메서드를 재정의합니다.
     * <p>
     * <b>[핵심 설계]</b> 이 엔티티의 비즈니스적 동일성은 고유 ID({@code id})가 아닌,
     * **'어떤 소설'과 '어떤 장르'의 조합**인지에 따라 결정됩니다.
     * 이를 통해, {@code Set}과 같은 컬렉션에서 "하나의 소설은 동일한 장르를 중복해서 가질 수 없다"는
     * 비즈니스 규칙을 객체 수준에서 보장할 수 있습니다.
     *
     * @param o 비교할 객체
     * @return 동일한 소설과 장르의 조합이면 {@code true}, 그렇지 않으면 {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NovelGenres that)) return false;
        // id 필드는 비교에서 제외하고, 비즈니스 키인 novel과 genre만으로 동등성을 판단
        return Objects.equals(novel, that.novel) && Objects.equals(genre, that.genre);
    }

    /**
     * {@code equals} 메서드를 재정의할 때 반드시 함께 재정의해야 하는 {@code hashCode} 메서드입니다.
     * <p>
     * {@code equals} 비교에 사용된 필드들({@code novel}, {@code genre})을 사용하여 해시 코드를 생성하여,
     * {@code HashMap}, {@code HashSet} 등에서 객체가 올바르게 동작하도록 보장합니다.
     *
     * @return 생성된 해시 코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(novel, genre);
    }
}
