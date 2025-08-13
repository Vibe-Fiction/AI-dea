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
 * 소설의 개별 회차(Chapter)와 관련된 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 특정 소설에 대한 회차 생성과 같은 작업을 담당합니다.
 * 모든 엔드포인트는 JWT 기반의 사용자 인증을 필요로 하며,
 * 주로 소설의 작가(Author)만이 관련 작업을 수행할 수 있습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels/{novelId}/chapters")
public class ChapterControllerTj {

    private final ChapterServiceTj chapterServiceTj;
    private final JwtHelperTj jwtHelperTj;

    /**
     * 특정 소설에 새로운 회차를 생성하고 등록합니다.
     *
     * 이 엔드포인트는 소설의 작가(Author)만 호출할 수 있습니다.
     * 요청 헤더의 JWT를 통해 사용자 인증을 수행하고, 인증된 사용자가 해당 소설의 작가인지
     * 서비스 레이어에서 검증합니다.
     *
     * @param authHeader HTTP 요청 헤더의 'Authorization' 값. 'Bearer {token}' 형식의 JWT가 포함되어야 합니다.
     * @param novelId    새로운 회차를 추가할 부모 소설의 고유 ID.
     * @param req        생성할 회차의 제목(title)과 내용(content)을 포함하는 요청 DTO.
     * @return 성공 시 201 (Created) 상태 코드와 함께 생성된 회차 정보를 담은 {@link ApiResponse} 객체를 반환합니다.
     *         인증 실패 시 401 (Unauthorized), 권한 부족 시 403 (Forbidden) 등의 상태 코드가 반환될 수 있습니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChapterCreateResponseTj>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long novelId,
            @Valid @RequestBody ChapterCreateRequestTj req) {

        // [TJ] 요청 헤더의 JWT에서 사용자(작가) ID를 추출
        Long authorId = jwtHelperTj.extractUserId(authHeader);

        // [비즈니스 규칙] 유효하지 않은 토큰으로 authorId를 추출할 수 없는 경우, 비인가 사용자로 처리
        if (authorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("인증에 실패했습니다."));
        }

        // [비즈니스 로직] 서비스 레이어로 소설 ID, 작가 ID, 요청 데이터를 전달하여 회차 생성 로직 수행
        // TODO(#77): chapterServiceTj.create()의 마지막 인자(file)가 현재 null로 고정되어 있음. 파일 업로드 기능 구현 시 수정 필요.
        ChapterCreateResponseTj result = chapterServiceTj.create(novelId, authorId, req, null);

        // [비즈니스 규칙] 리소스가 성공적으로 생성되었음을 의미하는 201 Created 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("새로운 회차가 등록되었습니다.", result));
    }
}
