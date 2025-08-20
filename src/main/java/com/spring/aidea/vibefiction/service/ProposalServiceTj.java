package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalSummaryResponseTj;
import com.spring.aidea.vibefiction.entity.AiInteractionLogs;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.AiInteractionLogsRepository;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자의 '이어쓰기 제안(Proposal)' 생성 및 조회를 위한 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * 이 서비스는 제안 생성 시 필요한 여러 도메인 엔티티(회차, 사용자, AI 로그)의 정합성을 검증하고,
 * 특히 AI 추천을 기반으로 한 제안을 실제 데이터로 변환하고, 그 관계를 설정하는 중요한 역할을 수행합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class ProposalServiceTj {

    /** 새로운 제안을 영속화하고, 특정 회차의 제안 목록을 조회하는 데 사용됩니다. */
    private final ProposalsRepository proposalsRepository;
    /** 새로운 제안을 연결할 대상 회차({@link Chapters})가 유효한지 검증하기 위해 사용됩니다. */
    private final ChaptersRepository chaptersRepository;
    /** 제안을 작성한 사용자({@link Users})의 유효성을 검증하고 엔티티를 조회하기 위해 사용됩니다. */
    private final UsersRepository usersRepository;
    /** 제안의 출처가 되는 AI 상호작용 로그({@link AiInteractionLogs})를 조회하고 연결하기 위해 사용됩니다. */
    private final AiInteractionLogsRepository aiInteractionLogsRepository;
    /**
     투표 마감일(voteDeadline) 설정 로직을 위해 VoteServiceMj 추가
     @songeky06(송민재)
     */
    private final VoteServiceMj voteServiceMj;
    /**
     * 새로운 이어쓰기 제안을 생성하고, 필요 시 AI 상호작용 로그와 연결합니다.
     * <p>
     * 하나의 트랜잭션으로 묶여 원자성을 보장하며, 데이터의 일관성을 유지합니다.
     * 엔티티 생성 로직은 {@link Proposals#create} 정적 팩토리 메서드에 위임하여 도메인의 응집도를 높입니다.
     *
     * @param chapterId  제안을 작성할 대상 {@link Chapters}의 고유 ID.
     * @param proposerId 제안을 작성한 {@link Users}의 고유 ID.
     * @param req        제안 생성에 필요한 데이터를 담은 {@link ProposalCreateRequestTj}.
     * @return           생성된 제안의 고유 ID를 담은 {@link ProposalCreateResponseTj}.
     * @throws BusinessException 관련 엔티티(회차, 사용자, AI로그)를 찾을 수 없을 경우.
     */
    @Transactional
    public ProposalCreateResponseTj create(Long chapterId, Long proposerId, ProposalCreateRequestTj req) {
        // [1. 선행 조건 검증: 연관 엔티티 조회]
        Users proposer = usersRepository.findById(proposerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Chapters chapter = chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "대상 회차를 찾을 수 없습니다."));

        // [선택적 로직: AI 로그 조회] AI 로그 ID가 있는 경우에만 해당 로그를 조회합니다.
        AiInteractionLogs aiLog = null;
        if (req.getAiLogId() != null) {
            aiLog = aiInteractionLogsRepository.findById(req.getAiLogId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "AI 로그를 찾을 수 없습니다."));
        }

        // [2. 도메인 객체 생성 (DDD)] 엔티티 생성 책임을 각 도메인의 정적 팩토리 메서드에 위임
        Proposals proposal = Proposals.create(chapter, proposer, req.getTitle(), req.getContent(), aiLog);


        /**
         요청하신 투표 마감일(voteDeadline) 설정 로직 추가
         @songkey06 (송민재)
         */
        int minutesToAdd = 1; // n을 1분으로 설정 (테스트 용)
        LocalDateTime voteDeadline = LocalDateTime.now().plusMinutes(minutesToAdd);
        proposal.setVoteDeadline(voteDeadline);

        // [3. 영속화] 생성된 제안 엔티티를 데이터베이스에 저장
        proposalsRepository.save(proposal);

        // [4. 양방향 연관관계 설정 (Dirty Checking)]
        // AI 로그가 있다면, AI 로그 엔티티에도 방금 생성된 제안을 연결해줍니다.
        // 이 변경사항은 트랜잭션 커밋 시점에 JPA의 변경 감지(Dirty Checking)에 의해 자동으로 UPDATE 쿼리가 실행됩니다.
        if (aiLog != null) {
            aiLog.setRelatedProposal(proposal);
        }

        // ✅ [추가] chapter 엔티티에서 novelId를 가져옵니다.
        Long novelId = chapter.getNovel().getNovelId();


        // [5. 결과 반환] 클라이언트에게 생성된 리소스의 고유 ID를 전달
        return new ProposalCreateResponseTj(proposal.getProposalId(), novelId);
    }

    /**
     * 특정 회차에 등록된 모든 제안의 목록을 요약된 형태로 조회합니다.
     * <p>
     * JPA의 변경 감지(Dirty Checking)를 생략하는 읽기 전용 트랜잭션({@code readOnly = true})으로 동작하여
     * 불필요한 오버헤드를 줄이고 조회 성능을 최적화합니다. 또한, 제안의 전체 내용(content)을 제외한
     * 요약 정보({@link ProposalSummaryResponseTj})만을 반환하여 네트워크 트래픽을 감소시킵니다.
     *
     * @param chapterId 제안 목록을 조회할 {@link Chapters}의 고유 ID.
     * @return          제안 요약 정보를 담은 DTO 리스트. 제안이 없으면 빈 리스트를 반환합니다.
     * @throws BusinessException 요청한 회차가 존재하지 않을 경우.
     */
    @Transactional(readOnly = true)
    public List<ProposalSummaryResponseTj> list(Long chapterId) {
        // [선행 조건 검증] 목록을 조회하기 전, 대상 회차가 실제로 존재하는지 먼저 확인하여 명확한 예외를 발생시킵니다.
        if (!chaptersRepository.existsById(chapterId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "제안 목록을 조회할 회차를 찾을 수 없습니다.");
        }

        // [데이터 조회] Repository를 통해 조건에 맞는 제안 엔티티 목록을 '등록순'으로 조회합니다.
        List<Proposals> proposals = proposalsRepository.findByChapter_ChapterIdOrderByCreatedAtAsc(chapterId);

        // [DTO 변환] 조회된 엔티티 목록(Proposals)을 순회하며, 클라이언트에게 전달할 요약 DTO(ProposalSummaryResponseTj) 목록으로 변환합니다.
        return proposals.stream()
                .map(p -> ProposalSummaryResponseTj.builder()
                        .proposalId(p.getProposalId())
                        .title(p.getTitle())
                        .voteCount(p.getVoteCount())
                        .status(p.getStatus().name())
                        .aiGenerated(p.getAiGenerated())
                        .createdAt(p.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
