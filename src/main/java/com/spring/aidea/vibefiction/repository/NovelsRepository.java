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
     * @return 소설id, 제목, 원작자id, 원작자닉네임, 시놉시스,
     *          커버이미지URL, 연재상태, 조회수, 장르, 마지막 업데이트
     */
    // 소설 전체조회
    // @EntityGraph는 적어둔 필드에 필요한 테이블을 자동으로 Fetch Join을 해줌
    @EntityGraph(attributePaths = {"author", "novelGenres.genre"})
    @Query("select distinct n from Novels n") // 카티샨 곱 문제 해결을 위한 distinct 적용
    List<Novels> findAllDetail();

    /**
     *
     * @param novelId
     * @return 소설id, 제목, 원작자id, 원작자닉네임, 시놉시스,
     *            커버이미지URL, 연재상태, 조회수, 장르, 마지막 업데이트
     */
    // 소설 단건 디테일 조회
    @EntityGraph(attributePaths = {"author", "novelGenres.genre"})
    @Query("select distinct n from Novels n where n.novelId = :novelId ")
    Optional<Novels> findByIdWithDetails(@Param("novelId") Long novelId);


}
