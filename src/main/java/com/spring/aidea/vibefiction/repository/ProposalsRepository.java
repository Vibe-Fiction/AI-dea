package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Proposals;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProposalsRepository extends JpaRepository<Proposals, Long> {
    /**
     * 특정 회차에 등록된 모든 이어쓰기 제안을 생성일 순(오름차순)으로 정렬하여 조회합니다.
     * <p>
     * 이 메서드는 특정 회차의 제안 목록을 '등록순'으로 보여주는 기본 조회 기능에 사용됩니다.
     * JPA 쿼리 메서드 규칙에 따라, 가장 먼저 등록된 제안부터 순서대로 반환합니다.
     * <p>
     * <b>[성능 경고]</b>
     * 하나의 회차에 수백, 수천 개의 제안이 등록될 수 있는 서비스 특성상, 이 메서드는 모든 제안을 메모리에 로드하므로
     * 잠재적인 성능 저하 및 메모리 부족(OOM) 문제를 유발할 수 있습니다. 데이터가 많아질 경우, 반드시
     * {@link org.springframework.data.domain.Pageable}을 인자로 받는 페이징(Paging) 버전의 메서드를 사용해야 합니다.
     *
     * @param chapterId 조회할 대상 {@link com.spring.aidea.vibefiction.entity.Chapters}의 고유 ID.
     * @return 해당 회차의 모든 {@link com.spring.aidea.vibefiction.entity.Proposals} 목록. 생성일(createdAt)이 오래된 순으로 정렬됩니다.
     *         제안이 하나도 없는 경우 빈 {@code List}가 반환됩니다.
     * @author 왕택준
     * @since 2025.08
     */
    List<Proposals> findByChapter_ChapterIdOrderByCreatedAtAsc(Long chapterId);

    // VoteServiceMj에서 사용될 메서드 추가
    // 챕터 ID로 제안을 조회하되, 투표 수(voteCount) 내림차순으로 정렬
    // `Pageable` 객체를 사용해 최대 6개만 가져오도록 함
    List<Proposals> findByChapter_ChapterIdOrderByVoteCountDesc(Long chapterId, Pageable pageable);

    // 투표 마감일이 현재 시간보다 이전이고, 상태가 VOTING인 제안들을 찾는 메서드
    List<Proposals> findByVoteDeadlineBeforeAndStatus(LocalDateTime now, Proposals.Status status);

    List<Proposals> findByChapter_ChapterIdAndProposalIdNotInOrderByVoteCountDesc(Long chapterId, List<Long> excludedProposalIds, PageRequest of);

    List<Proposals> findByChapter_ChapterId(Long chapterId);
}
