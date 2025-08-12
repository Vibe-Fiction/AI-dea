package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.novel.NovelCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.novel.NovelCreateResponseTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/novels")
public class NovelControllerTj {

    @PostMapping
    public ResponseEntity<NovelCreateResponseTj> create(@Valid @RequestBody NovelCreateRequestTj req) {
        // TODO(TJ): Jwt → userId 추출 후 NovelServiceTj.create(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
