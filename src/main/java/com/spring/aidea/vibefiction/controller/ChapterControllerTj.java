package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterCreateResponseTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/novels/{novelId}/chapters")
public class ChapterControllerTj {

    @PostMapping
    public ResponseEntity<ChapterCreateResponseTj> create(@PathVariable Long novelId,
                                                          @Valid @RequestBody ChapterCreateRequestTj req) {
        // TODO(TJ): Jwt → authorId 추출 후 ChapterServiceTj.create(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
