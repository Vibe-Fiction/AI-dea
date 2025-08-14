package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.novel.NovelCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.novel.NovelCreateResponseTj;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.service.NovelServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 소설(Novel)의 생성, 조회, 수정, 삭제(CRUD)와 관련된 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 소설 작품 자체에 대한 핵심적인 관리를 담당합니다.
 * 인증은 Spring Security의 JwtAuthenticationFilter를 통해 처리됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelControllerTj {

    private final NovelServiceTj novelServiceTj;
    private final UsersRepository usersRepository;

    /**
     * 인증된 사용자의 새로운 소설 작품을 생성합니다.
     *
     * @param req 생성할 소설의 메타데이터(제목, 시놉시스, 장르 등)와 1화 정보를 담은 요청 DTO.
     * @return 성공 시 201 (Created) 상태 코드와 함께 생성된 소설 정보를 담은 {@link ApiResponse} 객체를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NovelCreateResponseTj>> create(
        @Valid @RequestBody NovelCreateRequestTj req) {

        // SecurityContextHolder를 통해 인증된 사용자의 ID를 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Users author = usersRepository.findByLoginId(loginId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "인증된 사용자를 DB에서 찾을 수 없습니다."));
        Long authorId = author.getUserId();

        // 인증된 사용자(작가)의 ID와 소설 생성 요청 데이터를 서비스 레이어로 전달하여 처리
        NovelCreateResponseTj result = novelServiceTj.create(authorId, req);

        // 소설 리소스가 성공적으로 생성되었음을 알리는 201 Created 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("새로운 소설이 성공적으로 생성되었습니다.", result));
    }
}
