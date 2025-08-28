package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.vote.VoteFinalizeRequestMj;
import com.spring.aidea.vibefiction.dto.request.vote.VoteRequestMj;
import com.spring.aidea.vibefiction.dto.response.vote.VoteListAndClosingResponseMj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.service.VoteServiceMj;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
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


    /**
     * 투표 마감 처리 API
     * POST /api/vote/finalize
     * 클라이언트(vote-page.js)의 타이머 마감 시점에서 호출
     */
    @PostMapping("/finalize")
    public ResponseEntity<String> finalizeVoting(@RequestBody VoteFinalizeRequestMj request) {
        log.info("투표 마감 처리 요청: novelId={}", request.getNovelId());
        try {
            Long newChapterNovelId = voteServiceMj.finalizeVoting(request.getNovelId());

            if (newChapterNovelId != null) {
                // 성공: 새 챕터 생성 후 리다이렉트 URL을 JSON에 포함하여 반환
                String redirectUrl = "http://localhost:9009/chapters?novelId=" + newChapterNovelId;
                return ResponseEntity.ok().body("{\"message\": \"투표 결과가 반영되어 새 챕터가 생성되었습니다.\\n소설 페이지로 이동합니다.\", \"redirectUrl\": \"" + redirectUrl + "\"}");
            } else {
                // 실패 1: 동률 또는 무투표
                String redirectUrl = "http://localhost:9009/chapters?novelId=" + request.getNovelId();
                return ResponseEntity.ok().body("{\"message\": \"투표가 마감되었습니다.\\n\\n아쉽게도 이번 투표는 최다 득표작이 없어 새로운 챕터가 이어지지 않았습니다\\n소설 페이지로 이동합니다.\", \"redirectUrl\": \"" + redirectUrl + "\"}");
            }

        } catch (IllegalArgumentException e) {
            // 실패 2: 잘못된 요청
            log.error("잘못된 투표 마감 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // 실패 3: 서버 내부 오류
            log.error("투표 마감 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"투표 마감 처리 중 서버 오류가 발생했습니다. 다시 시도해주십시오.\"}");
        }
    }
}
