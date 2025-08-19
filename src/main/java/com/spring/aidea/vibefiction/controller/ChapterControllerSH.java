package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.response.chapter.ChapterResponseSH;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.service.ChapterServiceSH;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chapters")
public class ChapterControllerSH {




    private final ChapterServiceSH chapterServiceSH;




    /**
     *
     * 챕터페이지 렌더링을 위한 데이터를 반환합니다.
     *
     *
     * @param novelId
     * @return -
     * 예시  {
     *         "chapterId": 3,
     *         "novelId": 3,
     *         "chapterNumber": 1,
     *         "title": "입학 통지서",
     *         "content": "내 이름이 적힌 붉은 봉랍의 편지. 그것은 마법학교 에테르의 입학 통지서였다.",
     *         "author": "이야기꾼조씨" - 1회차인 경우에는 원작자의 이름이 들어갑니다.
     *     }
     */
    @GetMapping("/{novelId}")
    public ResponseEntity<?> findChaptersByNovelID(@PathVariable Long novelId) {


        List<ChapterResponseSH> chapters = chapterServiceSH.findChaptersByNovelID(novelId);
        if (chapters.isEmpty()) throw new BusinessException(ErrorCode.CHAPTER_NOT_FOUND);

        return ResponseEntity.ok().body(chapters);
    }
}
