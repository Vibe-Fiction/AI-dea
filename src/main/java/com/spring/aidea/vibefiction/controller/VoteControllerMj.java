package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.vote.VoteRequestMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.service.VoteServiceMj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VoteControllerMj {

    private final VoteServiceMj voteServiceMj;

    /**
     * 특정 소설의 투표 제안 목록을 페이지네이션하여 조회합니다.
     * 첫 요청 시 page=0, size=6으로 호출하여 무한 스크롤을 시작합니다.
     * ex) GET /api/vote/novels/1/proposals?page=0&size=6
     */
    @GetMapping("/novels/{novelId}/proposals")
    public ApiResponse<VoteListAndClosingResponseMj> getProposalsByNovelId(
        @PathVariable Long novelId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "6") int size) {

        VoteListAndClosingResponseMj response = voteServiceMj.getVoteDataForNovel(novelId, page, size);

        // 제안 목록이 비어 있으면 "더 이상 투표 제안이 없습니다." 메시지를 반환
        if (response.getProposals().isEmpty()) {
            return ApiResponse.failure("더 이상 투표 제안이 없습니다.");
        }

        return ApiResponse.success("투표 제안 목록을 성공적으로 조회했습니다.", response);
    }

    /**
     * 투표 요청 API
     * POST /api/vote/do
     */
    @PostMapping("/do")
    public ResponseEntity<String> doVote(@RequestBody VoteRequestMj request, @AuthenticationPrincipal User currentUser) {
        String loginId = currentUser.getUsername();

        try {
            voteServiceMj.createVote(request.getProposalId(), loginId);
            return ResponseEntity.ok("투표가 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 제안 ID 등
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 투표가 이미 완료된 경우, 또는 투표 기간이 지난 경우
            // 예외 메시지에 따라 상태 코드를 다르게 반환
            if (e.getMessage().equals("이미 투표에 참여했습니다.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } else if (e.getMessage().equals("투표 기간이 마감되었습니다.")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 투표 취소 API
     * DELETE /api/vote/do
     */
    @DeleteMapping("/do")
    public ResponseEntity<String> cancelVote(@RequestBody VoteRequestMj request, @AuthenticationPrincipal User currentUser) {
        String loginId = currentUser.getUsername();

        try {
            voteServiceMj.cancelVote(request.getProposalId(), loginId);
            return ResponseEntity.ok("투표가 성공적으로 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 제안 ID 또는 투표 기록이 없는 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
