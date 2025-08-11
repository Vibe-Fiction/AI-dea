package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Novels;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface NovelsRepository extends JpaRepository<Novels, Long> {


    /**
     *
     * @return 소설id, 원작자id, 제목, 시놉시스, 커버이미지URL, 공개설정, 연재상태, 조회수, 마지막 업데이트, 장르, 좋아요
     */
    // 소설 전체조회
    // @EntityGraph는 적어둔 필드에 필요한 테이블을 자동으로 Fetch Join을 해줌
    @Query(value = """
SELECT
    N.NOVEL_ID,
    N.TITLE,
    N.SYNOPSIS,
    N.COVER_IMAGE_URL,
    N.VISIBILITY,
    N.STATUS,
    N.VIEW_COUNT,
    N.CREATED_AT,
    N.LAST_UPDATED_AT,
    N.AUTHOR_ID,
    COUNT(CASE WHEN F.CATEGORY = 'LIKE' THEN 1 END) AS like_count,
    GROUP_CONCAT(DISTINCT G.NAME) AS GENRE_NAMES,
    GROUP_CONCAT(DISTINCT C.USER_ID) AS COLLABORATOR_USER_IDS
FROM novels N
         LEFT JOIN novel_genres NG ON N.NOVEL_ID = NG.NOVEL_ID
         LEFT JOIN GENRES G ON NG.GENRE_ID = G.GENRE_ID
         LEFT JOIN FAVORITES F ON N.NOVEL_ID = F.NOVEL_ID
         LEFT JOIN COLLABORATORS C ON N.NOVEL_ID = C.NOVEL_ID
GROUP BY N.NOVEL_ID
""", nativeQuery=true) // 카티샨 곱 문제 해결을 위한 distinct 적용
    List<Novels> findAllWithGenresAndAuthor();

    // 소설 단건 디테일 조회
    @EntityGraph(attributePaths = {"author", "novelGenres", "collaborators","favorites"})
    @Query("""
select distinct n from Novels n where n.novelId = :novelId
"""
    )
    Optional<Novels> findByIdWithDetails(@Param("novelId") Long novelId);


}
