package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.novel.NovelCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.novel.NovelCreateResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.jwt.JwtHelperTj;
import com.spring.aidea.vibefiction.service.NovelServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * 소설(Novels) 관련 CRUD를 처리하는 컨트롤러.
 *
 * - 새로운 소설 생성
 * - JWT 인증 필요
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/novels")
public class NovelControllerTj {

    private final NovelServiceTj novelServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 새로운 소설을 생성합니다.
     * 요청에 1화 제목 및 내용을 포함하면, 소설 생성과 동시에 1화도 함께 생성됩니다.
     *
     * @param authHeader Authorization 헤더 (Bearer {JWT})
     * @param req        소설 생성 요청 DTO (제목, 시놉시스, 장르 목록, 1화 정보)
     * @return           생성된 소설 정보 (1화 포함 가능)
     * @status 201       소설 생성 성공
     * @status 401       인증 실패
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NovelCreateResponseTj>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody NovelCreateRequestTj req) {

        Long authorId = jwtHelperTj.extractUserId(authHeader);
        if (authorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다. 유효하지 않은 토큰입니다."));
        }

        NovelCreateResponseTj result = novelServiceTj.create(authorId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("새로운 소설이 성공적으로 생성되었습니다.", result));
    }
}
