package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.response.genre.GenreResponse;
import com.spring.aidea.vibefiction.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 장르(Genre) 정보와 관련된 API 요청을 처리하는 컨트롤러입니다.
 * <p>
 * 현재는 프론트엔드의 장르 선택 UI에 필요한 전체 장르 목록을 제공하는 기능을 담당합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * 시스템에 등록된 모든 장르의 목록을 조회합니다.
     * <p>
     * 이 API는 인증 없이 호출 가능하며, 클라이언트가 장르 선택 UI를 구성하는 데 사용됩니다.
     *
     * @return {@link GenreResponse} DTO 객체의 리스트. 각 객체는 Enum 상수명(code)과 한글 설명(description)을 포함합니다.
     */
    @GetMapping
    public List<GenreResponse> getGenres() {
        return genreService.getAllGenres();
    }
}
