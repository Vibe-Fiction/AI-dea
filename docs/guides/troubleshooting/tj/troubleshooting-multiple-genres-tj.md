# 트러블슈팅: 여러 장르 저장 시 첫 번째만 저장되는 문제

본 문서는 **Team Ai-dea**의 **Vibe Fiction 프로젝트**(Relai 플랫폼) 개발 과정에서 소설 생성 시 여러 장르를 선택했음에도 DB에 항상 첫 번째 장르만 저장되는 문제를 분석하고 해결한 사례를 정리한 기술 문서입니다.

**작성자:** [왕택준](https://github.com/TJK98)

**작성일:** 2025년 8월 19일

**문서 버전:** v1.0

---

## 1. 문제 현상

* 프론트엔드에서 소설 생성 시 배열 형태로 여러 장르를 전송함

  ```json
  "genres": ["FANTASY", "ROMANCE", "MYSTERY"]
  ```
* DTO(`NovelCreateRequestTj`)에서는 `List<String> genres`로 정상적으로 수신됨
* Service 계층을 거쳐 `Novels.create()`까지는 배열이 그대로 전달됨
* 그러나 **DB에는 항상 첫 번째 장르만 저장됨**

---

## 2. 원인 분석

### 2-1. 데이터 흐름 점검

* 프론트 → DTO → Service → `Novels.create()`까지는 문제 없음
* **엔티티 저장 단계**에서 손실 발생

### 2-2. 기존 엔티티 구조

```java
@OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
@Builder.Default
private Set<NovelGenres> novelGenres = new HashSet<>();

// Novels.create()
novel.novelGenres = genres.stream()
    .map(genre -> NovelGenres.create(novel, genre))
    .collect(Collectors.toSet());
```

* 설계 의도: 여러 장르 → `NovelGenres` 매핑 엔티티로 변환 후 `Set`에 저장
* 실제 동작: **첫 번째 장르만 유지, 나머지는 제거**

### 2-3. 원인 규명

* `NovelGenres` 엔티티에 존재한 코드:

  ```java
  @EqualsAndHashCode(exclude = {"novel", "genre"})
  ```
* 즉, `id`만 기준으로 동등성 비교 수행
* 문제점:

  * 새로 생성된 `NovelGenres`는 아직 `id = null`
  * Hibernate가 모든 객체를 동일하다고 판단
  * `HashSet`은 첫 번째 객체만 유지 → 나머지 삭제

---

## 3. 해결 과정

### 3-1. 잘못된 코드 제거

```java
@EqualsAndHashCode(exclude = {"novel", "genre"})  // ❌ 제거
```

### 3-2. 올바른 equals/hashCode 구현

```java
@Entity
@Table(name = "novel_genres",
       uniqueConstraints = @UniqueConstraint(columnNames = {"novel_id", "genre_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"novel", "genre"})
public class NovelGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_genres_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novels novel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genres genre;

    public static NovelGenres create(Novels novel, Genres genre) {
        return NovelGenres.builder()
                .novel(novel)
                .genre(genre)
                .build();
    }

    // (novel, genre) 조합으로 동등성 비교
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NovelGenres)) return false;
        NovelGenres that = (NovelGenres) o;
        return novel.equals(that.novel) && genre.equals(that.genre);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(novel, genre);
    }
}
```

---

## 4. 결론 및 배운 점

* **문제 원인**: 잘못된 `equals/hashCode` 구현으로 인해 JPA `Set` 컬렉션이 모든 엔티티를 동일하다고 인식 → 첫 번째만 저장
* **해결책**: `(novel, genre)` 조합 기반으로 `equals/hashCode` 재구현
* **변경 효과**:

  * 여러 장르 선택 시 모든 조합이 `Set`에 정상적으로 추가
  * DB에도 모든 장르가 INSERT됨
  * `uniqueConstraints`와 `equals/hashCode` 일치로 데이터 정합성 보장
* **배운 점**: JPA 엔티티에서 `equals/hashCode`는 단순히 IDE 자동 생성에 의존하면 안 되고, **비즈니스 키**를 기준으로 직접 정의해야 한다.
