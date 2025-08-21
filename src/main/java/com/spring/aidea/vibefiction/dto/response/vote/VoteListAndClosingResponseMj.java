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

    // ✅ [추가] 최신 챕터 ID 필드 추가
    private Long latestChapterId;
}
