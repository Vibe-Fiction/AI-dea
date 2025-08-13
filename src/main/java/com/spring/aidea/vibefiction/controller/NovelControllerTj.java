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
 * 소설(Novel)의 생성, 조회, 수정, 삭제(CRUD)와 관련된 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 소설 작품 자체에 대한 핵심적인 관리를 담당합니다.
 * 대부분의 엔드포인트는 JWT 기반의 사용자 인증을 필요로 하며,
 * 특정 작업(예: 생성, 수정)은 소설의 작가(Author) 권한을 요구합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelControllerTj {

    private final NovelServiceTj novelServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 인증된 사용자의 새로운 소설 작품을 생성합니다.
     *
     * 이 메서드는 소설의 기본 정보(제목, 시놉시스, 장르 등)와 함께 첫 번째 회차(1화) 정보를
     * 선택적으로 받아, 소설과 1화를 트랜잭션 내에서 동시에 생성할 수 있습니다.
     *
     * @param authHeader HTTP 요청 헤더의 'Authorization' 값. 'Bearer {token}' 형식의 JWT가 포함되어야 합니다.
     * @param req        생성할 소설의 메타데이터(제목, 시놉시스, 장르 등)와 1화 정보를 담은 요청 DTO.
     * @return 성공 시 201 (Created) 상태 코드와 함께 생성된 소설 정보를 담은 {@link ApiResponse} 객체를 반환합니다.
     *         인증에 실패하면 401 (Unauthorized) 상태 코드를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NovelCreateResponseTj>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Valid @RequestBody NovelCreateRequestTj req) {

        // [TJ] JWT 토큰을 이용해 요청한 사용자의 ID(authorId)를 추출
        Long authorId = jwtHelperTj.extractUserId(authHeader);

        // [비즈니스 규칙] authorId가 null인 경우, 유효하지 않은 토큰으로 판단하여 비인가(401) 처리
        if (authorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다. 유효하지 않은 토큰입니다."));
        }

        // [비즈니스 로직] 인증된 사용자(작가)의 ID와 소설 생성 요청 데이터를 서비스 레이어로 전달하여 처리
        NovelCreateResponseTj result = novelServiceTj.create(authorId, req);

        // [비즈니스 규칙] 소설 리소스가 성공적으로 생성되었음을 알리는 201 Created 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("새로운 소설이 성공적으로 생성되었습니다.", result));
    }
}
