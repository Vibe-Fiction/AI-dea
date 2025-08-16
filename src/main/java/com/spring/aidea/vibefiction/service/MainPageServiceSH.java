package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.novel.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.custom.NovelsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MainPageServiceSH {


    /**
     *  novelsRepository : 데이터베이스에서 novels 정보를 조회하기 위한 저장소입니다.
     */
    private final NovelsRepository novelsRepository;

    public List<NovelsResponseDtoSH> findAllNovels(Pageable pageable) {

        List<Novels> novelList = novelsRepository.findAllNovelsPage(pageable);


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

