package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.AiContinueRequestTj;
import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.AiRecommendNovelRequestTj;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.AiContinueResponseTj;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.AiRecommendNovelResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.jwt.JwtHelperTj;
import com.spring.aidea.vibefiction.service.AiAssistServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI 관련 기능(소설 추천, 이어쓰기 제안)을 처리하는 컨트롤러입니다.
 * 모든 요청은 JWT 기반 인증을 필요로 합니다.
 *
 * 주요 기능:
 * - AI 기반 새 소설 추천
 * - AI 기반 이어쓰기 추천
 *
 * 인증 방식:
 * - HTTP Authorization 헤더에 Bearer 토큰을 포함해야 함
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/ai")
public class AiControllerTj {

    private final AiAssistServiceTj aiAssistServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * AI를 통해 새 소설의 제목과 1화 내용을 추천합니다.
     *
     * @param authHeader JWT 인증 토큰 (Authorization: Bearer {token})
     * @param req        소설 장르와 시놉시스 요청 DTO
     * @return           추천된 소설 제목과 1화 내용
     * @status 200       성공 시 추천 결과 반환
     * @status 401       인증 실패
     */
    @PostMapping("/novels/recommend")
    public ResponseEntity<ApiResponse<AiRecommendNovelResponseTj>> recommend(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody AiRecommendNovelRequestTj req) {

        // JWT 토큰에서 사용자 ID 추출 및 인증 검증
        Long userId = jwtHelperTj.extractUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다."));
        }

        AiRecommendNovelResponseTj responseData = aiAssistServiceTj.recommendForNewNovel(userId, req);
        return ResponseEntity.ok(ApiResponse.success("AI 소설 추천 성공", responseData));
    }

    /**
     * AI를 통해 특정 회차의 이어쓰기 내용을 추천합니다.
     *
     * @param authHeader JWT 인증 토큰 (Authorization: Bearer {token})
     * @param chapterId  이어쓰기를 진행할 회차 ID
     * @param req        이어쓰기 요청 DTO (작성 가이드라인, 추가 설정 등 포함)
     * @return           추천된 이어쓰기 내용
     * @status 200       성공 시 추천 결과 반환
     * @status 401       인증 실패
     */
    @PostMapping("/chapters/{chapterId}/continue")
    public ResponseEntity<ApiResponse<AiContinueResponseTj>> cont(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long chapterId,
            @Valid @RequestBody AiContinueRequestTj req) {

        // JWT 토큰에서 사용자 ID 추출 및 인증 검증
        Long userId = jwtHelperTj.extractUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다."));
        }

        AiContinueResponseTj responseData = aiAssistServiceTj.continueForChapter(userId, chapterId, req);
        return ResponseEntity.ok(ApiResponse.success("AI 이어쓰기 추천 성공", responseData));
    }
}
