package com.spring.aidea.vibefiction.dto.response.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteProposalResponseMj {
    private Long proposalId;
    private Long chapterId;
    private String novelName;
    private String proposalTitle;
    private String proposalAuthor;
    private String proposalContent;  // 본문은 모달 전용
    private Integer voteCount;
}
