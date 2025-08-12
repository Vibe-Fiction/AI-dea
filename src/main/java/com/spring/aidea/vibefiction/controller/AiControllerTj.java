package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.*;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/ai")
public class AiControllerTj {

    @PostMapping("/novels/recommend")
    public ResponseEntity<AiRecommendNovelResponseTj> recommend(@Valid @RequestBody AiRecommendNovelRequestTj req) {
        // TODO(TJ): Jwt → userId 추출 후 AiAssistServiceTj.recommendForNewNovel(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/chapters/{chapterId}/continue")
    public ResponseEntity<AiContinueResponseTj> cont(@PathVariable Long chapterId,
                                                   @Valid @RequestBody AiContinueRequestTj req) {
        // TODO(TJ): Jwt → userId 추출 후 AiAssistServiceTj.continueForChapter(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
