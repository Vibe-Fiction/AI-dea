package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.entity.Novels;
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

        List<Novels> novelList = novelsRepository.findAllWithGenresAndAuthor();

        return novelList.stream()
                .map(NovelsResponseDtoSH::from)
                .toList();

    }
}

