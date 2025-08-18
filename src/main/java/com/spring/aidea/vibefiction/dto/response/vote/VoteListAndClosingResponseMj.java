package com.spring.aidea.vibefiction.dto.response.vote;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class VoteListAndClosingResponseMj {
    private List<VoteProposalResponseMj> proposals;
    private VoteClosingResponseMj deadlineInfo;
}
