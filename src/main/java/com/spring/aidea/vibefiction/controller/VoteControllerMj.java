package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.response.vote.VoteProposalResponseMj;
import com.spring.aidea.vibefiction.service.VoteServiceMj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VoteControllerMj {

    private final VoteServiceMj voteService;

    /**
     * 소설 ID 기반으로 해당 소설의 마지막 회차 → 제안 목록 조회
     * ex) GET /api/vote/novels/1/proposals
     */
    @GetMapping("/novels/{novelId}/proposals")
    public ResponseEntity<List<VoteProposalResponseMj>> getProposalsByNovelId(@PathVariable Long novelId) {
        List<VoteProposalResponseMj> response = voteService.getProposalsForNovel(novelId);
        return ResponseEntity.ok(response);
    }
}
