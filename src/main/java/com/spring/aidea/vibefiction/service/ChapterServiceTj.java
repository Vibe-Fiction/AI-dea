package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterCreateResponseTj;
import org.springframework.stereotype.Service;

/** [TJ] 회차 생성 서비스 (구현은 추후) */
@Service
public class ChapterServiceTj {
    public ChapterCreateResponseTj create(Long novelId, Long authorId, ChapterCreateRequestTj req, Long fromProposalId) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }
}
