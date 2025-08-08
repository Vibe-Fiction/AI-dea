package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@EqualsAndHashCode(of = "genreId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Comment("장르 마스터 테이블")
public class Genres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id", columnDefinition = "INT")
    @Comment("장르 ID")
    private Integer genreId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Comment("장르 이름")
    private String name;

    // --- 연관관계 ---
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelGenres> novelGenres = new ArrayList<>();

}