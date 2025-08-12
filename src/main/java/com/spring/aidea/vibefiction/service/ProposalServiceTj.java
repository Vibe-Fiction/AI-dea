package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalSummaryResponseTj;
import org.springframework.stereotype.Service;

import java.util.List;

/** [TJ] 제안 서비스 (구현은 추후) */
@Service
public class ProposalServiceTj {
    public ProposalCreateResponseTj create(Long chapterId, Long proposerId, ProposalCreateRequestTj req) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }
    public List<ProposalSummaryResponseTj> list(Long chapterId) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }
}
