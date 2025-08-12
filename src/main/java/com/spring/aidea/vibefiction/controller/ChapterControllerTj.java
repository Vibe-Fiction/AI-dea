package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterCreateResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.jwt.JwtHelperTj;
import com.spring.aidea.vibefiction.service.ChapterServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 소설의 회차(Chapters) 관련 CRUD를 처리하는 컨트롤러.
 *
 * - 특정 소설에 새로운 회차를 추가
 * - JWT 인증 필요
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/novels/{novelId}/chapters")
public class ChapterControllerTj {

    private final ChapterServiceTj chapterServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 특정 소설에 새로운 회차를 추가합니다.
     *
     * @param authHeader Authorization 헤더 (Bearer {JWT})
     * @param novelId    회차를 추가할 소설 ID
     * @param req        회차 생성 요청 DTO (제목, 내용)
     * @return           생성된 회차 정보
     * @status 201       회차 생성 성공
     * @status 401       인증 실패
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChapterCreateResponseTj>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long novelId,
            @Valid @RequestBody ChapterCreateRequestTj req) {

        Long authorId = jwtHelperTj.extractUserId(authHeader);
        if (authorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증 실패"));
        }

        ChapterCreateResponseTj result = chapterServiceTj.create(novelId, authorId, req, null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("새로운 회차가 등록되었습니다.", result));
    }
}
