// src/test/java/com/spring/aidea/vibefiction/service/VoteServiceMjTest.java

package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceMjTest {

    @Mock
    private NovelsRepository novelsRepository;

    @Mock
    private ChaptersRepository chaptersRepository;

    @Mock
    private ProposalsRepository proposalsRepository;

    @InjectMocks
    private VoteServiceMj voteServiceMj;

    @Test
    void testGetVoteDataForNovel_SortByVoteCountDesc() {
        // given
        Long novelId = 1L;
        Long chapterId = 10L;

        // 1. 테스트용 엔티티 생성
        Users user = Users.builder().userId(1L).nickname("테스트유저").build();
        Novels novel = Novels.builder().novelId(novelId).title("테스트 소설").build();
        Chapters chapter = Chapters.builder().chapterId(chapterId).createdAt(LocalDateTime.now()).novel(novel).build();

        // 2. 투표 수 기준 정렬을 위한 가짜 데이터 생성
        Proposals p1 = Proposals.builder().proposalId(1L).chapter(chapter).proposer(user).title("제안1").voteCount(50).build();
        Proposals p2 = Proposals.builder().proposalId(2L).chapter(chapter).proposer(user).title("제안2").voteCount(80).build();
        Proposals p3 = Proposals.builder().proposalId(3L).chapter(chapter).proposer(user).title("제안3").voteCount(20).build();
        Proposals p4 = Proposals.builder().proposalId(4L).chapter(chapter).proposer(user).title("제안4").voteCount(100).build();

        List<Proposals> mockProposals = Arrays.asList(p4, p2, p1, p3); // voteCount 내림차순으로 정렬된 상태

        // 3. Mock 객체들의 동작 설정
        when(novelsRepository.findById(anyLong())).thenReturn(Optional.of(novel));
        when(chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(anyLong())).thenReturn(Optional.of(chapter));
        when(proposalsRepository.findByChapter_ChapterIdOrderByVoteCountDesc(anyLong(), any(PageRequest.class))).thenReturn(mockProposals);

        // when
        VoteListAndClosingResponseMj result = voteServiceMj.getVoteDataForNovel(novelId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProposals()).hasSize(4); // 4개의 제안이 반환되었는지 확인
        assertThat(result.getProposals().get(0).getVoteCount()).isEqualTo(100); // 첫 번째 제안이 가장 높은 투표 수인지 확인
        assertThat(result.getProposals().get(1).getVoteCount()).isEqualTo(80);
        assertThat(result.getProposals().get(2).getVoteCount()).isEqualTo(50);
        assertThat(result.getProposals().get(3).getVoteCount()).isEqualTo(20);
    }
}
