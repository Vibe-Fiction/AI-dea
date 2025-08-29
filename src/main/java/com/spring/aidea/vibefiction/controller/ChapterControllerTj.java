package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterResponseSH;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.service.ChapterServiceSH;
import com.spring.aidea.vibefiction.service.ChapterServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 소설의 개별 회차(Chapter)와 관련된 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 특정 소설에 대한 회차 생성과 같은 작업을 담당합니다.
 * 인증은 Spring Security의 JwtAuthenticationFilter를 통해 처리됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels/{novelId}/chapters")
public class ChapterControllerTj {

    private final ChapterServiceTj chapterServiceTj;
    private final UsersRepository usersRepository;

    /**
     * 특정 소설에 새로운 회차를 생성하고 등록합니다.
     *
     * @param novelId    새로운 회차를 추가할 부모 소설의 고유 ID.
     * @param req        생성할 회차의 제목(title)과 내용(content)을 포함하는 요청 DTO.
     * @return 성공 시 201 (Created) 상태 코드와 함께 생성된 회차 정보를 담은 {@link ApiResponse} 객체를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChapterCreateResponseTj>> create(
        @PathVariable Long novelId,
        @Valid @RequestBody ChapterCreateRequestTj req) {

        // SecurityContextHolder를 통해 인증된 사용자의 ID를 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Users author = usersRepository.findByLoginId(loginId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "인증된 사용자를 DB에서 찾을 수 없습니다."));
        Long authorId = author.getUserId();

        // 서비스 레이어로 소설 ID, 작가 ID, 요청 데이터를 전달하여 회차 생성 로직 수행
        ChapterCreateResponseTj result = chapterServiceTj.create(novelId, authorId, req, null);

        // 리소스가 성공적으로 생성되었음을 의미하는 201 Created 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("새로운 회차가 등록되었습니다.", result));
    }










}
