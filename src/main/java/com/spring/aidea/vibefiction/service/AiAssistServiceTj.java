package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.*;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.*;
import org.springframework.stereotype.Service;

/** [TJ] AI 보조 서비스 (구현은 추후) */
@Service
public class AiAssistServiceTj {
    public AiRecommendNovelResponseTj recommendForNewNovel(Long userId, AiRecommendNovelRequestTj req) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }
    public AiContinueResponseTj continueForChapter(Long userId, Long chapterId, AiContinueRequestTj req) {
        throw new UnsupportedOperationException("NOT_IMPLEMENTED");
    }
}
