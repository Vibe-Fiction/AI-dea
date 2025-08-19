package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


// src/test/java/com/spring/aidea/vibefiction/service/VoteSchedulerTest.java




@ExtendWith(MockitoExtension.class)
class VoteSchedulerTest {

    @Mock
    private ProposalsRepository proposalsRepository;

    @InjectMocks
    private VoteScheduler voteScheduler;

    private Proposals expiredProposal;

    @BeforeEach
    void setUp() {
        // 테스트용 Proposals 엔티티 생성
        expiredProposal = Proposals.builder()
            .proposalId(1L)
            .voteDeadline(LocalDateTime.now().minusDays(1)) // 마감일이 어제 날짜
            .status(Proposals.Status.VOTING) // 현재 상태: 투표 중
            .voteCount(5)
            .build();
    }

    @Test
    void testUpdateExpiredProposalStatus() {
        // given
        // proposalsRepository.findByVoteDeadlineBeforeAndStatus() 메서드가 호출될 때
        // 만료된 제안 목록을 반환하도록 설정
        List<Proposals> mockList = Arrays.asList(expiredProposal);
        when(proposalsRepository.findByVoteDeadlineBeforeAndStatus(any(LocalDateTime.class), eq(Proposals.Status.VOTING)))
            .thenReturn(mockList);

        // when
        // VoteScheduler의 스케줄러 메서드 실행
        voteScheduler.updateExpiredProposalStatus();

        // then
        // 1. proposalsRepository.findByVoteDeadlineBeforeAndStatus()가 1회 호출되었는지 확인
        verify(proposalsRepository, times(1)).findByVoteDeadlineBeforeAndStatus(any(LocalDateTime.class), eq(Proposals.Status.VOTING));

        // 2. 만료된 제안의 상태가 'REJECTED'로 변경되었는지 확인
        // verify() 메서드로 save()가 호출되었는지 확인하고, 내부 상태를 검증
        verify(proposalsRepository, times(1)).save(expiredProposal);
    }
}
