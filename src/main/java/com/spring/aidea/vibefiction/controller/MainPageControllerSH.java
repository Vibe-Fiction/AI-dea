package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public class MainPageControllerSH {


    @RestController
    @RequestMapping("/api/novels")
    @Slf4j
    @RequiredArgsConstructor
    public class MainPageControllerSH {

        private final MainPageService mainPageService;




        @GetMapping
        public ResponseEntity<?> findAllNovels() {

            List<NovelsResponseDto> allNovels = mainPageService.findAllNovels();

            if(allNovels.isEmpty()) throw new BusinessException(ErrorCode.NOVEL_NOT_FOUND);

            return ResponseEntity.ok(allNovels);

        }
    }


}
