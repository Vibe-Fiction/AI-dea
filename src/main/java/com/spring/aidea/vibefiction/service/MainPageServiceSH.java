package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MainPageServiceSH {

    private final NovelsRepository novelsRepository;

    public List<NovelsResponseDtoSH> findAllNovels() {

        List<Novels> novelList = novelsRepository.findAllDetail();

        return novelList.stream()
                .map(NovelsResponseDtoSH::from)
                .toList();
    }

    public NovelsResponseDtoSH findNovelById(Long novelId) {
        Novels novels = novelsRepository.findById(novelId)
                .orElseThrow(() ->new BusinessException(ErrorCode.NOVEL_NOT_FOUND));

        return NovelsResponseDtoSH.from(novels);
    }



}

