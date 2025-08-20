package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.vote.VoteClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteProposalResponseMj;
import com.spring.aidea.vibefiction.entity.*;
import com.spring.aidea.vibefiction.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteServiceMj {

    private final NovelsRepository novelsRepository;
    private final ChaptersRepository chaptersRepository;
    private final ProposalsRepository proposalsRepository;
    private final UsersRepository usersRepository;
    private final VotesRepository votesRepository;

    /**
     * 마지막 챕터에 대한 투표 데이터와 마감 시간을 조회하고 페이지네이션을 지원합니다.
     * @param novelId 소설 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 제안 개수
     * @return 투표 목록 및 마감 시간 응답 DTO
     */
    public VoteListAndClosingResponseMj getVoteDataForNovel(Long novelId, int page, int size) {
        // 소설의 마지막 챕터를 조회. 챕터가 없는 경우에도 null이 아닌 Optional.empty()를 반환하도록 수정
        Chapters lastChapter = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElse(null); // ✅ [수정] 챕터가 없을 경우 예외 대신 null을 반환

        // 페이지네이션된 제안 조회
        List<VoteProposalResponseMj> proposals = getTopProposalsAndConvertToDto(lastChapter.getChapterId(), page, size);

        VoteClosingResponseMj deadlineResponse = null;

        // 마지막 챕터 아이디
        Long latestChapterId = null;

        if (lastChapter != null) {
            proposals = getTopProposalsAndConvertToDto(lastChapter.getChapterId(), page, size);
            LocalDateTime deadline = getVotingDeadline(lastChapter);
            latestChapterId = lastChapter.getChapterId();

            // ✅ [수정] 제안이 있을 경우에만 마감 시간 정보를 설정
            if (!proposals.isEmpty()) {
                deadlineResponse = VoteClosingResponseMj.builder()
                    .chapterId(lastChapter.getChapterId())
                    .closingTime(deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            }
        }

        // ✅ [수정] 제안 목록이 비어있더라도 latestChapterId는 항상 포함하여 반환
        return VoteListAndClosingResponseMj.builder()
            .proposals(proposals)
            .deadlineInfo(deadlineResponse)
            .latestChapterId(latestChapterId) // ✅ [수정] 최신 챕터 ID를 항상 포함
            .build();
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

    private Chapters validateNovelAndGetLastChapter(Long novelId) {
        novelsRepository.findById(novelId)
            .orElseThrow(() -> new IllegalArgumentException("소설이 존재하지 않습니다. novelId=" + novelId));
        return chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
            .orElseThrow(() -> new IllegalStateException("아직 회차가 없습니다. novelId=" + novelId));
    }

    // 본 서비스에서는 등록날짜에서 3일 더하기지만 테스트를 위해 주석처리
    private LocalDateTime getVotingDeadline(Chapters lastChapter) {
        return lastChapter.getCreatedAt().plusDays(3);
    }

    // 테스트 용으로 등록 시점에서 3분
    /*private LocalDateTime getVotingDeadline(Chapters lastChapter) {
            return lastChapter.getCreatedAt().plusMinutes(3);
    }*/

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
