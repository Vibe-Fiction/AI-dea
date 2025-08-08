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
@ToString(exclude = {"novels", "genre"}) // 무한 재귀 방지를 위해 연관관계 필드 제외
@EqualsAndHashCode(exclude = {"novels", "genre"}) // 무한 재귀 방지를 위해 연관관계 필드 제외
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
    private Novels novels;

    // 장르 ID (복합 키의 일부)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    @Comment("장르 ID")
    private Genres genre;

    // TODO: 연관관계 편의 메서드 추가 가능
}
