package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.chapter.ChapterResponseSH;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChapterServiceSH {

    private final ChaptersRepository chaptersRepository;



    public List<ChapterResponseSH> findChaptersByNovelID(Long novelId){

        return chaptersRepository.findAllChaptersAndAuthorNameByNovelId(novelId);
    }

}
