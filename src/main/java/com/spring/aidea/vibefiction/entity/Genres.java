package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * 소설의 장르 마스터 정보를 관리하는 엔티티 클래스입니다.
 * <p>
 * [리팩토링]
 * 장르의 이름을 나타내는 {@code name} 필드가 기존의 {@code String} 타입에서
 * 내부적으로 정의된 {@link GenreType} Enum 타입으로 변경되었습니다.
 * 이는 장르 데이터의 정합성과 타입 안정성을 코드 레벨에서 보장하기 위함입니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Entity
@Table(name = "genres")
@Getter
@EqualsAndHashCode(of = "genreId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = "novelGenres")
@Comment("장르 마스터 테이블")
public class Genres {

    /**
     * 플랫폼에서 사용될 소설 장르의 종류를 정의하는 열거형(Enum)입니다.
     * <p>
     * 각 Enum 상수는 데이터베이스의 {@code name} 컬럼에 문자열로 저장되며({@code FANTASY}, {@code ROMANCE} 등),
     * 사용자에게 보여줄 한글 설명({@code description})을 함께 가집니다.
     * 이 Enum은 {@code Genres} 엔티티 내부에서만 사용되어 높은 응집도를 유지합니다.
     */
    @Getter
    @RequiredArgsConstructor
    public enum GenreType {
        // --- 대중적인 메인 장르 ---
        FANTASY("판타지"),
        ROMANCE("로맨스"),
        MODERN_FANTASY("현대판타지"),
        MARTIAL_ARTS("무협"),
        MYSTERY("미스터리"),
        HORROR("호러"),
        SCI_FI("SF"),
        THRILLER("스릴러"),
        ACTION("액션"),
        COMEDY("코미디"),
        DRAMA("드라마"),
        HISTORICAL("시대극/역사"),

        // --- 웹소설 인기 하위 장르/설정 ---
        ROMANCE_FANTASY("로맨스판타지"),
        GAME_FANTASY("게임판타지"),
        REGRESSION("회귀"),
        REINCARNATION("빙의/환생"),
        ACADEMY("아카데미"),
        APOCALYPSE("아포칼립스"),

        // --- 팬덤/특정 취향 장르 ---
        BL("BL"),
        GL("GL"),

        // --- 기타 ---
        SLICE_OF_LIFE("일상"),
        SHORT_STORY("단편");

        private final String description;
    }

    /**
     * 장르의 고유 식별자(Primary Key)입니다. 데이터베이스에 의해 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id", columnDefinition = "INT")
    @Comment("장르 ID")
    private Integer genreId;

    /**
     * [리팩토링-TO-BE] GenreType Enum 필드
     * <p>
     * {@link GenreType} Enum을 사용하여 장르의 종류를 코드 레벨에서 강제합니다.
     * {@code @Enumerated(EnumType.STRING)} 설정을 통해, 데이터베이스의 'name' 컬럼에는
     * Enum의 상수명(e.g., "FANTASY")이 문자열로 저장됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Comment("장르 이름")
    private GenreType name;

    /**
     * 이 장르를 가지고 있는 소설들과의 연결 관계(N:M)를 나타내는 필드입니다.
     * {@link NovelGenres} 연결 엔티티를 통해 관리됩니다.
     */
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NovelGenres> novelGenres = new ArrayList<>();

    /*
     * [리팩토링-AS-IS] 기존 String 타입 name 필드
     *
     * @author 왕택준
     * 장르 이름을 자유로운 문자열로 저장하는 기존 방식은 데이터의 오타나 비일관성을 막을 수 없습니다.
     * 이를 해결하기 위해, 이 클래스 내부에 사전 정의된 GenreType Enum을 사용하여
     * 데이터의 정합성과 타입 안정성을 확보하는 방식으로 필드 타입을 변경했습니다.
     *
    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Comment("장르 이름")
    private String name;
    */
}
