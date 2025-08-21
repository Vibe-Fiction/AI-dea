package com.spring.aidea.vibefiction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteProposalResponseMj;
import com.spring.aidea.vibefiction.entity.*;
import com.spring.aidea.vibefiction.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VoteServiceMj {

    private final NovelsRepository novelsRepository;
    private final ChaptersRepository chaptersRepository;
    private final ProposalsRepository proposalsRepository;
    private final UsersRepository usersRepository;
    private final VotesRepository votesRepository;
    private final ChapterServiceTj chapterServiceTj;


    /**
     * 마지막 챕터에 대한 투표 데이터와 마감 시간을 조회하고 페이지네이션을 지원합니다.
     * @param novelId 소설 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 제안 개수
     * @return 투표 목록 및 마감 시간 응답 DTO
     */
    public VoteListAndClosingResponseMj getVoteDataForNovel(Long novelId, int page, int size) {
        Chapters lastChapter = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElse(null);

        List<VoteProposalResponseMj> proposals = Collections.emptyList();
        VoteClosingResponseMj deadlineResponse = null;
        Long latestChapterId = null;

        if (lastChapter != null) {
            proposals = getTopProposalsAndConvertToDto(lastChapter.getChapterId(), page, size);
            latestChapterId = lastChapter.getChapterId();

            // 제안 유무와 관계없이 마감 시간 정보 생성
            LocalDateTime deadline = getVotingDeadline(lastChapter);
            deadlineResponse = VoteClosingResponseMj.builder()
                .chapterId(lastChapter.getChapterId())
                .closingTime(deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        }

        // JSON 객체를 콘솔에 출력하는 로깅
        VoteListAndClosingResponseMj response = VoteListAndClosingResponseMj.builder()
            .proposals(proposals)
            .deadlineInfo(deadlineResponse)
            .latestChapterId(latestChapterId)
            .build();

        return response;
    }



    /**
     * 투표 기능: 로그인 확인, 중복 투표 방지 후 투표 기록
     * @param proposalId 투표할 제안 ID
     * @param loginId 현재 로그인한 사용자의 ID
     */
    @Transactional
    public void createVote(Long proposalId, String loginId) {
        Users user = usersRepository.findByLoginId(loginId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Proposals proposal = proposalsRepository.findById(proposalId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 제안입니다."));

        Chapters chapter = proposal.getChapter();
        LocalDateTime deadline = getVotingDeadline(chapter);


        // 투표마감 시간 후 투표 방지
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalStateException("투표 기간이 마감되었습니다.");
        }

        // 1. 중복 투표 방지 (동일 제안에 대한 재투표 방지)
        boolean hasVotedOnThisProposal = votesRepository.existsByUserAndProposal(user, proposal);
        if (hasVotedOnThisProposal) {
            throw new IllegalStateException("이미 투표에 참여했습니다.");
        }

        // 2. 해당 챕터 내 다른 제안에 투표했는지 확인
        // 이 기능을 구현하려면 Vote 엔티티에 chapter 필드를 추가하거나,
        // VotesRepository에 사용자 ID와 챕터 ID로 투표 기록을 찾는 커스텀 쿼리를 추가해야 합니다.
        // 예를 들어, votesRepository.existsByUserIdAndChapterId(user.getUserId(), chapter.getChapterId()) 와 같은 메서드가 필요합니다.
        boolean hasVotedInThisChapter = votesRepository.existsByUser_UserIdAndProposal_Chapter_ChapterId(user.getUserId(), chapter.getChapterId());
        if (hasVotedInThisChapter) {
            throw new IllegalStateException("해당 챕터의 다른 제안에 이미 투표했습니다.");
        }

        // ✅ [추가] 자신의 제안에 투표하는 것을 방지
        if (proposal.getProposer().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("자신의 제안에는 투표할 수 없습니다.");
        }

        Votes newVote = Votes.builder()
            .user(user)
            .proposal(proposal)
            .build();
        votesRepository.save(newVote);

        proposal.incrementVoteCount();
    }


    /**
     * 투표 취소 기능: 투표 기록 삭제 및 제안 투표 수 감소
     * @param proposalId 투표를 취소할 제안 ID
     * @param loginId 현재 로그인한 사용자의 ID
     */
    @Transactional
    public void cancelVote(Long proposalId, String loginId) {
        Users user = usersRepository.findByLoginId(loginId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Proposals proposal = proposalsRepository.findById(proposalId)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 제안입니다."));

        // 1. 해당 사용자의 해당 제안에 대한 투표 기록을 찾습니다.
        Votes vote = votesRepository.findByUserAndProposal(user, proposal)
            .orElseThrow(() -> new IllegalArgumentException("취소할 투표 기록이 존재하지 않습니다."));

        // 2. 투표 기록을 삭제합니다.
        votesRepository.delete(vote);

        // 3. 해당 제안의 투표 수를 1 감소시킵니다.
        proposal.decrementVoteCount();
    }

    /*private Chapters validateNovelAndGetLastChapter(Long novelId) {
        novelsRepository.findById(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설이 존재하지 않습니다. novelId=" + novelId));
        return chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElseThrow(() -> new IllegalStateException("아직 회차가 없습니다. novelId=" + novelId));
    }*/

    //lastChapter의 생성일로부터 3일을 더하고, 시간을 23:59:58로 설정합니다.
    /*private LocalDateTime getVotingDeadline(Chapters lastChapter) {
            LocalDateTime deadline = lastChapter.getCreatedAt()
                    .plusDays(3)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(58)
                    .withNano(0); // 나노초를 0으로 설정하여 일관성을 유지합니다.

            return deadline;
        }*/

    // 테스트 용으로 등록 시점에서 1분
    LocalDateTime getVotingDeadline(Chapters lastChapter) {
            return lastChapter.getCreatedAt().plusMinutes(1);
    }

    /**
     * @description 투표 마감 처리 및 새로운 챕터 생성 로직을 담당합니다.
     * @param novelId 소설 ID
     * @param loginId 현재 로그인한 사용자의 ID (여기서는 투표 마감 권한 확인용)
     */
    @Transactional
    public void finalizeVoting(Long novelId, String loginId) {
        log.info("소설 ID {}에 대한 투표 마감 처리 시작", novelId);

        // 1. 소설의 마지막 챕터 조회
        Chapters lastChapter = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설의 마지막 챕터를 찾을 수 없습니다."));

        // 2. 해당 챕터의 모든 제안 조회
        List<Proposals> allProposals = proposalsRepository.findByChapter_ChapterId(lastChapter.getChapterId());
        if (allProposals.isEmpty()) {
            log.warn("소설 ID {}의 마지막 챕터에 등록된 제안이 없습니다.", novelId);
            return;
        }

        // 3. 최다 득표 제안(들) 찾기
        Integer maxVotes = allProposals.stream()
            .map(Proposals::getVoteCount)
            .max(Comparator.naturalOrder())
            .orElse(0);

        List<Proposals> topProposals = allProposals.stream()
            .filter(p -> p.getVoteCount().equals(maxVotes))
            .collect(Collectors.toList());

        // 4. `relay_automation_rules.md`의 규칙 적용
        if (topProposals.size() == 1) { // 4-1. 단독 최다 득표
            Proposals adoptedProposal = topProposals.get(0);
            adoptedProposal.setStatus(Proposals.Status.ADOPTED);
            log.info("단독 최다 득표 제안이 채택되었습니다. 제안 ID: {}", adoptedProposal.getProposalId());

            // 나머지 제안은 REJECTED로 변경
            allProposals.stream()
                .filter(p -> !p.getProposalId().equals(adoptedProposal.getProposalId()))
                .forEach(p -> p.setStatus(Proposals.Status.REJECTED));

            // 5. 채택된 제안으로 새로운 챕터 생성
            ChapterCreateRequestTj createRequest = ChapterCreateRequestTj.builder()
                .title(adoptedProposal.getTitle())
                .content(adoptedProposal.getContent())
                .build();

            chapterServiceTj.create(
                adoptedProposal.getChapter().getNovel().getNovelId(),
                adoptedProposal.getProposer().getUserId(),
                createRequest,
                adoptedProposal.getProposalId()
            );

        } else if (topProposals.size() > 1) { // 4-2. 동률(복수 최다)
            topProposals.forEach(p -> p.setStatus(Proposals.Status.PENDING));
            log.info("최다 득표 동률 발생. 동률 제안 {}개를 PENDING 상태로 변경합니다.", topProposals.size());
            // 나머지 제안은 REJECTED로 변경
            allProposals.stream()
                .filter(p -> !topProposals.contains(p))
                .forEach(p -> p.setStatus(Proposals.Status.REJECTED));

        } else { // 4-3. 무투표 동률 (모든 제안 투표수 0)
            allProposals.forEach(p -> p.setStatus(Proposals.Status.PENDING));
            log.info("무투표 또는 모든 제안 투표수 0. 모든 제안을 PENDING 상태로 변경합니다.");
        }
    }



    //JSOM안에 내용 담는 함수
    private List<VoteProposalResponseMj> getTopProposalsAndConvertToDto(Long chapterId, int page, int size) {
        if (chapterId == null) {
            return Collections.emptyList();
        }

        Novels novel = chaptersRepository.findById(chapterId)
            .map(Chapters::getNovel)
            .orElseThrow(() -> new IllegalArgumentException("챕터가 존재하지 않습니다."));

        List<Proposals> proposals = proposalsRepository.findByChapter_ChapterIdOrderByVoteCountDesc(chapterId, PageRequest.of(page, size));

        return proposals.stream()
            .map(p -> VoteProposalResponseMj.builder()
                .proposalId(p.getProposalId())
                .chapterId(chapterId)
                .novelName(novel.getTitle())
                .proposalTitle(p.getTitle())
                .proposalAuthor(p.getProposer().getNickname())
                .proposalContent(p.getContent())
                .voteCount(p.getVoteCount())
                .build())
            .collect(Collectors.toList());
    }
}

