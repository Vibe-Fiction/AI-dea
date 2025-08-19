// ChaptersRepository.java
package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.repository.custom.ChaptersRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChaptersRepository extends JpaRepository<Chapters, Long> , ChaptersRepositoryCustom {
    /**
     * 지정된 소설에서 가장 마지막 회차(가장 높은 회차 번호를 가진 회차)를 조회합니다.
     * <p>
     * <b>[JPA 쿼리 메서드]</b>
     * 이 메서드는 {@code novel_id}를 기준으로 {@code chapterNumber}를 내림차순 정렬한 후,
     * 가장 첫 번째 결과(LIMIT 1)를 가져옵니다. 회차가 하나도 없는 경우 {@link Optional#empty()}를 반환합니다.
     * <p>
     * <b>[설계 변경: AS-IS &rarr; TO-BE]</b>
     * <ul>
     *  <li><b>AS-IS:</b> {@code findTopByNovelIdOrderByChapterNumberDesc(Long novelId)}
     *      <br>&rarr; {@code Chapters} 엔티티에 {@code novelId} 필드가 직접 존재해야 함.</li>
     *  <li><b>TO-BE:</b> {@code findTopByNovel_NovelIdOrderByChapterNumberDesc(Long novelId)}
     *      <br>&rarr; {@code Chapters}의 연관 필드인 {@code novel}을 통해 그 안의 {@code novelId}까지 탐색(Traversal)하여 조회.</li>
     * </ul>
     * 이 변경을 통해 필드를 중복해서 갖지 않고, 객체 그래프 탐색을 이용한 JPA의 장점을 살려
     * 더 객체지향적인 쿼리를 작성할 수 있습니다.
     *
     * @param novelId 회차를 조회할 대상 소설의 고유 ID.
     * @return 조회된 가장 마지막 {@link Chapters} 엔티티. 해당 소설에 회차가 없으면 {@link Optional#empty()}.
     * @author 왕택준
     * @since 2025.08
     */
    Optional<Chapters> findTopByNovel_NovelIdOrderByChapterNumberDesc(Long novelId);
}
