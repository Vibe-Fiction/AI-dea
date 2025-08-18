package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.vote.VoteRequestMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.service.VoteServiceMj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<VoteListAndClosingResponseMj> getProposalsByNovelId(@PathVariable Long novelId) {
        VoteListAndClosingResponseMj response = voteService.getVoteDataForNovel(novelId);
        return ResponseEntity.ok(response);
    }

    /**
     * 투표 요청 API
     * POST /api/vote/do
     */
    @PostMapping("/do")
    public ResponseEntity<String> doVote(@RequestBody VoteRequestMj request, @AuthenticationPrincipal User currentUser) {
        // @AuthenticationPrincipal을 통해 현재 로그인한 사용자 정보(User 객체)를 가져옴
        String loginId = currentUser.getUsername();

        try {
            voteService.createVote(request.getProposalId(), loginId);
            return ResponseEntity.ok("투표가 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
