package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class MainPageServiceSH {
    @Transactional
    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class MainPageService {

        private final NovelsRepository novelsRepository;

        public List<NovelsResponseDto> findAllNovels() {

            List<Novels> novelList = novelsRepository.findAllWithGenresAndAuthor();

            return novelList.stream()
                    .map(NovelsResponseDto::from)
                    .toList();

        }
    }

}
