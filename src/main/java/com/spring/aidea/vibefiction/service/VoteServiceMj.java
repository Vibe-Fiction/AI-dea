
package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.vote.VoteProposalResponseMj;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import com.spring.aidea.vibefiction.repository.VotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 추가
public class VoteServiceMj {

    private final NovelsRepository novelsRepository;
    private final ChaptersRepository chaptersRepository;
    private final ProposalsRepository proposalsRepository;
    private final VotesRepository votesRepository;

    /**
     * 소설 ID로 투표 리스트(현재 경쟁중인 제안들) 조회
     */
    public List<VoteProposalResponseMj> getProposalsForNovel(Long novelId) {
        // 1. 소설 검증
        Novels novel = novelsRepository.findById(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설이 존재하지 않습니다. novelId=" + novelId));

        // 2. 마지막 회차(chapterId) 조회
        Chapters lastChapter = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElseThrow(() -> new IllegalStateException("아직 회차가 없습니다. novelId=" + novelId));

        // 3. 해당 회차의 proposal 6개 가져오기 (예시로 limit 6, 실제는 pageable 적용 권장)
        List<Proposals> proposals = proposalsRepository
            .findByChapter_ChapterIdOrderByCreatedAtAsc(lastChapter.getChapterId())
            .stream()
            .limit(6)
            .toList();

        // 4. DTO 변환
        return proposals.stream()
            .map(p -> VoteProposalResponseMj.builder()
                .proposalId(p.getProposalId())
                .chapterId(lastChapter.getChapterId())
                .novelName(novel.getTitle())
                .proposalTitle(p.getTitle())
                .proposalAuthor(p.getProposer().getNickname()) // Users 엔티티에 닉네임
                .voteCount(p.getVoteCount())
                .build())
            .toList();
    }
}
