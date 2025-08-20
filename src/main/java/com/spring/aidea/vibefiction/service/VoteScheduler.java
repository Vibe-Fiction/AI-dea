// com.spring.aidea.vibefiction.service.VoteScheduler.java

package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.repository.ProposalsRepository; // ProposalsRepository 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteScheduler {

    // VotesRepository 대신 ProposalsRepository를 사용
    private final ProposalsRepository proposalsRepository;

    // 매일 자정(23:59:59)에 실행
    @Scheduled(cron = "5 * * * * *")
    public void updateExpiredProposalStatus() {
        log.info("만료된 제안 상태 업데이트 스케줄러 시작");

        LocalDateTime now = LocalDateTime.now();

        // 투표 마감일이 지났고, 상태가 'VOTING'인 제안들을 조회
        List<Proposals> expiredProposals = proposalsRepository.findByVoteDeadlineBeforeAndStatus(now, Proposals.Status.VOTING);

        // 4-1. 제안이 0개면 아래 설명된 10번 로직 실행 (현재 로직 없음, 주석으로 표시)
        if (expiredProposals.isEmpty()) {
            log.info("만료된 투표 제안이 없습니다. 스케줄러 종료.");
            // TODO: (10번) 제안이 0개일 때의 추가 로직 구현
            return;
        }

        // 투표 수가 가장 많은 제안을 찾기 위해 정렬 (내림차순)
        expiredProposals.sort(Comparator.comparing(Proposals::getVoteCount).reversed());

        // 최다 득표수 가져오기
        int maxVoteCount = expiredProposals.get(0).getVoteCount();

        // 최다 득표수를 받은 제안 목록과 나머지 제안 목록을 분리
        List<Proposals> topProposals = expiredProposals.stream()
            .filter(p -> p.getVoteCount() == maxVoteCount)
            .collect(Collectors.toList());

        List<Proposals> rejectedProposals = expiredProposals.stream()
            .filter(p -> p.getVoteCount() < maxVoteCount)
            .collect(Collectors.toList());

        // 4-2. 제안이 1개 이상 투표수가 0인 경우 status='PENDING'으로 상태 변환
        if (topProposals.size() >= 1 && maxVoteCount == 0) {
            topProposals.forEach(p -> p.setStatus(Proposals.Status.PENDING));
            log.info("투표수가 0인 제안을 PENDING으로 업데이트 완료. 제안 수: {}", topProposals.size());
        }
        // 4-3. 제안이 2개 이상 투표수가 같은 경우 PENDING, 나머지는 REJECTED
        else if (topProposals.size() >= 2) {
            topProposals.forEach(p -> p.setStatus(Proposals.Status.PENDING));
            rejectedProposals.forEach(p -> p.setStatus(Proposals.Status.REJECTED));
            log.info("동점인 최다 득표 제안을 PENDING으로, 나머지를 REJECTED로 업데이트 완료.");
        }
        // 4-4. 제안이 투표수가 최다 득표가 한개인 경우 ADOPTED
        else { // topProposals.size() == 1
            topProposals.get(0).setStatus(Proposals.Status.ADOPTED);
            rejectedProposals.forEach(p -> p.setStatus(Proposals.Status.REJECTED));
            log.info("최다 득표 제안 1개를 ADOPTED로, 나머지를 REJECTED로 업데이트 완료.");
        }

        // 변경된 상태를 데이터베이스에 일괄 저장
        proposalsRepository.saveAll(expiredProposals);

        log.info("만료된 제안 상태 업데이트 스케줄러 종료. {}개의 제안이 업데이트되었습니다.", expiredProposals.size());
    }
}
