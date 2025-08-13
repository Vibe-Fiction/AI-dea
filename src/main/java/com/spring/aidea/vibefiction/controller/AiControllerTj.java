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
 * AI 기반의 보조 기능(소설 추천, 이어쓰기 제안) API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러의 모든 엔드포인트는 클라이언트의 요청 헤더에 유효한 JWT(Bearer 토큰)를
 * 포함해야만 접근이 가능합니다. 토큰 검증을 통해 인증된 사용자의 요청만 서비스 레이어로 전달됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiControllerTj {

    private final AiAssistServiceTj aiAssistServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 사용자가 입력한 장르와 시놉시스를 기반으로 AI가 새로운 소설의 제목과 1화 내용을 생성하여 추천합니다.
     *
     * 이 메서드는 인증된 사용자만 호출할 수 있으며, AI 모델과의 상호작용은 서비스 레이어에서 처리됩니다.
     * 성공 시, 생성된 제목과 내용을 포함한 응답을 반환합니다.
     *
     * @param authHeader HTTP 요청 헤더의 'Authorization' 값. 'Bearer {token}' 형식의 JWT가 포함되어야 합니다.
     * @param req        AI 추천에 필요한 소설 장르(genre)와 시놉시스(synopsis)를 담은 DTO.
     * @return 성공 시 200 (OK) 상태 코드와 함께 추천 결과를 담은 {@link ApiResponse} 객체를 반환합니다.
     *         인증 실패 시 401 (Unauthorized) 상태 코드를 반환합니다.
     */
    @PostMapping("/novels/recommend")
    public ResponseEntity<ApiResponse<AiRecommendNovelResponseTj>> recommend(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody AiRecommendNovelRequestTj req) {

        // [TJ] 요청 헤더의 JWT를 통해 사용자를 인증하는 로직
        Long userId = jwtHelperTj.extractUserId(authHeader);

        // [비즈니스 규칙] userId가 null인 경우(유효하지 않은 토큰)는 비인가 사용자로 간주하여 요청을 거부
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다."));
        }

        // [비즈니스 로직] 인증된 사용자의 ID와 요청 데이터를 서비스 레이어로 전달하여 AI 추천 로직 수행
        AiRecommendNovelResponseTj responseData = aiAssistServiceTj.recommendForNewNovel(userId, req);
        return ResponseEntity.ok(ApiResponse.success("AI 소설 추천 성공", responseData));
    }

    /**
     * 특정 소설 회차에 이어질 다음 내용을 AI가 생성하여 제안합니다.
     *
     * 사용자는 이어쓰기 가이드라인을 제공할 수 있으며, AI는 이를 참고하여 스토리를 생성합니다.
     * 이 메서드는 인증된 사용자만 호출할 수 있습니다.
     *
     * @param authHeader  HTTP 요청 헤더의 'Authorization' 값. 'Bearer {token}' 형식의 JWT가 포함되어야 합니다.
     * @param chapterId   이어쓰기를 제안받을 대상 회차의 고유 ID.
     * @param req         AI 이어쓰기 생성에 필요한 가이드라인(guideline) 등의 정보를 담은 DTO.
     * @return 성공 시 200 (OK) 상태 코드와 함께 제안된 내용을 담은 {@link ApiResponse} 객체를 반환합니다.
     *         인증 실패 시 401 (Unauthorized) 상태 코드를 반환합니다.
     */
    @PostMapping("/chapters/{chapterId}/continue")
    public ResponseEntity<ApiResponse<AiContinueResponseTj>> cont(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long chapterId,
            @Valid @RequestBody AiContinueRequestTj req) {

        // [TJ] 요청 헤더의 JWT를 통해 사용자를 인증하는 로직
        Long userId = jwtHelperTj.extractUserId(authHeader);

        // [비즈니스 규칙] userId가 null인 경우(유효하지 않은 토큰)는 비인가 사용자로 간주하여 요청을 거부
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다."));
        }

        // [비즈니스 로직] 인증된 사용자 ID, 대상 회차 ID, 요청 데이터를 서비스 레이어로 전달하여 AI 이어쓰기 로직 수행
        AiContinueResponseTj responseData = aiAssistServiceTj.continueForChapter(userId, chapterId, req);
        return ResponseEntity.ok(ApiResponse.success("AI 이어쓰기 추천 성공", responseData));
    }
}
