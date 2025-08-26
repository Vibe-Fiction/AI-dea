package com.spring.aidea.vibefiction.dto.response.vote;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.util.List;

@Getter
@Data
@Builder
public class VoteListAndClosingResponseMj {
    private List<VoteProposalResponseMj> proposals;
    private VoteClosingResponseMj deadlineInfo;
    private Long latestChapterId;
    private boolean isVotingClosed; // 추가
}
