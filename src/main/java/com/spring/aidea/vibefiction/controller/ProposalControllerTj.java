package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalSummaryResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.jwt.JwtHelperTj;
import com.spring.aidea.vibefiction.service.ProposalServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 이어쓰기 제안(Proposals) 관련 CRUD 컨트롤러.
 *
 * - 특정 회차에 제안 생성 (JWT 필요)
 * - 특정 회차의 제안 목록 조회 (공개 가정: 인증 불필요)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/chapters/{chapterId}/proposals")
public class ProposalControllerTj {

    private final ProposalServiceTj proposalServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 특정 회차에 새로운 이어쓰기 제안을 생성합니다.
     *
     * @param authHeader Authorization 헤더 (Bearer {JWT})
     * @param chapterId  제안을 생성할 회차 ID
     * @param req        제안 생성 요청 DTO (제목, 내용, 선택적 aiLogId)
     * @return           생성된 제안 정보
     * @status 201       생성 성공
     * @status 401       인증 실패
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProposalCreateResponseTj>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long chapterId,
            @Valid @RequestBody ProposalCreateRequestTj req) {

        Long proposerId = jwtHelperTj.extractUserId(authHeader);
        if (proposerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증 실패"));
        }

        ProposalCreateResponseTj result = proposalServiceTj.create(chapterId, proposerId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("새로운 제안이 등록되었습니다.", result));
    }

    /**
     * 특정 회차의 모든 제안 목록을 조회합니다. (공개 가정)
     *
     * @param chapterId 제안을 조회할 회차 ID
     * @return          제안 요약 목록
     * @status 200      조회 성공
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProposalSummaryResponseTj>>> list(@PathVariable Long chapterId) {
        List<ProposalSummaryResponseTj> result = proposalServiceTj.list(chapterId);
        return ResponseEntity.ok(ApiResponse.success("제안 목록 조회 성공", result));
    }
}
