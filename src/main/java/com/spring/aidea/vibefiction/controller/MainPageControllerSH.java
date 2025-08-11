package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.response.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.service.MainPageServiceSH;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/novels")
@Slf4j
@RequiredArgsConstructor
public class MainPageControllerSH {

    private final MainPageServiceSH mainPageServiceSH;


    @GetMapping
    public ResponseEntity<?> findAllNovels() {

        List<NovelsResponseDtoSH> allNovels = mainPageServiceSH.findAllNovels();

        if (allNovels.isEmpty()) throw new BusinessException(ErrorCode.NOVEL_NOT_FOUND);

        return ResponseEntity.ok(allNovels);

    }
}



