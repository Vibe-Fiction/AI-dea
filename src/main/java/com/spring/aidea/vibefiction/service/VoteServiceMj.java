package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.vote.VoteClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteProposalResponseMj;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceMj {

    private final NovelsRepository novelsRepository;
    private final ChaptersRepository chaptersRepository;
    private final ProposalsRepository proposalsRepository;

    /**
     * 소설 ID로 투표 리스트(현재 경쟁중인 제안들)와 마감 시간을 함께 조회
     * @param novelId 조회할 소설 ID
     * @return 투표 제안 목록 및 마감 시간을 담은 응답 DTO
     */
    public VoteListAndClosingResponseMj getVoteDataForNovel(Long novelId) {
        Chapters lastChapter = validateNovelAndGetLastChapter(novelId);
        LocalDateTime deadline = getVotingDeadline(lastChapter);

        // 제안 목록 조회 및 DTO 변환
        // getProposalsAndConvertToDto 메서드는 더 이상 deadline을 인자로 받지 않습니다.
        List<VoteProposalResponseMj> proposals = getProposalsAndConvertToDto(novelId, lastChapter);

        // 마감 시간 응답 DTO 생성 및 날짜 형식 지정
        VoteClosingResponseMj deadlineResponse = VoteClosingResponseMj.builder()
            .chapterId(lastChapter.getChapterId()) // chapterId 추가
            .closingTime(deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) // `LocalDateTime`을 `String`으로 변환
            .build();

        return VoteListAndClosingResponseMj.builder()
            .proposals(proposals)
            .deadlineInfo(deadlineResponse)
            .build();
    }

    /**
     * 소설 존재 여부를 검증하고, 가장 최근 회차를 반환합니다.
     * @param novelId 조회할 소설 ID
     * @return 최신 회차 엔티티
     */
    private Chapters validateNovelAndGetLastChapter(Long novelId) {
        novelsRepository.findById(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설이 존재하지 않습니다. novelId=" + novelId));

        return chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElseThrow(() -> new IllegalStateException("아직 회차가 없습니다. novelId=" + novelId));
    }

    /**
     * 투표 마감 시간을 계산하여 반환합니다.
     * @param lastChapter 최신 회차 엔티티
     * @return 투표 마감 시간 (최신 챕터 생성 시간 + 3일)
     */
    private LocalDateTime getVotingDeadline(Chapters lastChapter) {
        return lastChapter.getCreatedAt().plusDays(3);
    }

    /**
     * 마지막 챕터에 대한 제안 6개를 조회하고 DTO로 변환합니다.
     * @param novelId    소설 ID
     * @param lastChapter 마지막 회차 엔티티
     * @return 투표 제안 응답 DTO 리스트
     */
    private List<VoteProposalResponseMj> getProposalsAndConvertToDto(Long novelId, Chapters lastChapter) {
        Novels novel = novelsRepository.findById(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설이 존재하지 않습니다."));

        List<Proposals> proposals = proposalsRepository
            .findByChapter_ChapterIdOrderByCreatedAtAsc(lastChapter.getChapterId())
            .stream()
            .limit(6)
            .toList();

        return proposals.stream()
            .map(p -> VoteProposalResponseMj.builder()
                .proposalId(p.getProposalId())
                .chapterId(lastChapter.getChapterId())
                .novelName(novel.getTitle())
                .proposalTitle(p.getTitle())
                .proposalAuthor(p.getProposer().getNickname())
                .voteCount(p.getVoteCount())
                .build())
            .toList();
    }
}
