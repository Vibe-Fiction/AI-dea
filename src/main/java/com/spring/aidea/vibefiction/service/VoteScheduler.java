// com.spring.aidea.vibefiction.service.VoteScheduler.java

package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.repository.ProposalsRepository; // ProposalsRepository 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteScheduler {

    // VotesRepository 대신 ProposalsRepository를 사용
    private final ProposalsRepository proposalsRepository;

    // 매일 자정(23:59:59)에 실행
    @Scheduled(cron = "59 59 23 * * *")
    public void updateExpiredProposalStatus() {
        log.info("만료된 제안 상태 업데이트 스케줄러 시작");

        LocalDateTime now = LocalDateTime.now();

        // 투표 마감일이 지났고, 상태가 'VOTING'인 제안들을 조회
        List<Proposals> expiredProposals = proposalsRepository.findByVoteDeadlineBeforeAndStatus(now, Proposals.Status.VOTING);

        for (Proposals proposal : expiredProposals) {
            // 투표 상태를 REJECTED로 변경
            proposal.setStatus(Proposals.Status.REJECTED);

            // 엔티티를 저장하여 데이터베이스에 반영
            proposalsRepository.save(proposal);
        }

        log.info("만료된 제안 상태 업데이트 스케줄러 종료. {}개의 제안이 업데이트되었습니다.", expiredProposals.size());
    }
}
