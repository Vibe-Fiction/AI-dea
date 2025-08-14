package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenresRepository extends JpaRepository<Genres, Integer> {

    /**
     * [리팩토링] GenreType Enum 목록에 해당하는 Genres 엔티티 목록을 조회합니다.
     * Spring Data JPA의 'In' 키워드를 사용하여, 'WHERE name IN (?, ?, ...)' 쿼리를 자동으로 생성합니다.
     *
     * @param names 조회할 장르의 GenreType Enum 목록
     * @return 조회된 Genres 엔티티 목록
     *
     * @author 왕택준
     */
    List<Genres> findByNameIn(List<Genres.GenreType> names);

}
