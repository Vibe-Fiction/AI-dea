package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.entity.Votes;
import com.spring.aidea.vibefiction.repository.VotesRepository; // 투표 엔티티를 관리하는 Repository
import com.spring.aidea.vibefiction.entity.Status;
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

    private final VotesRepository voteRepository;

    // 매일 자정(00:00:00)에 이 메서드가 실행됩니다.
    // Cron 표현식은 "초 분 시 일 월 요일" 순서입니다.
    // 0 0 0 * * * 는 매일 0시 0분에 실행됨을 의미합니다.
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredVoteStatus() {
        log.info("만료된 투표 상태 업데이트 스케줄러 시작");

        // 현재 시간을 기준으로 만료된 투표를 찾아 상태를 변경합니다.
        LocalDateTime now = LocalDateTime.now();
        List<Votes> expiredVotes = voteRepository.findByEndTimeBeforeAndStatus(now, Status.VOTING);

        for (Votes vote : expiredVotes) {
            // 투표 종료 로직을 여기에 구현합니다.
            // 예를 들어, 투표 결과에 따라 ADOPTED 또는 REJECTED로 상태를 변경합니다.
            vote.setStatus(determineFinalStatus(vote)); // 상태 변경 로직
            voteRepository.save(vote);
        }

        log.info("만료된 투표 상태 업데이트 스케줄러 종료. {}개의 투표가 업데이트되었습니다.", expiredVotes.size());
    }

    private Status determineFinalStatus(Vote vote) {
        // 투표 결과를 기반으로 최종 상태를 결정하는 로직을 구현합니다.
        // 예: 찬성표가 반대표보다 많으면 ADOPTED, 아니면 REJECTED
        // 이 부분은 비즈니스 로직에 맞게 구현하세요.
        return Status.ADOPTED; // 예시
    }
}
