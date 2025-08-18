package com.spring.aidea.vibefiction.dto.response.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRendering {
    private Long ProposalId;
    private String ProposalTitle;
    private String ProposalAuthor;
    private String ProposalContent;
}
