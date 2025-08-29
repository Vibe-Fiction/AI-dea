package com.spring.aidea.vibefiction.repository.custom;

import com.spring.aidea.vibefiction.dto.response.chapter.ChapterResponseSH;
import com.spring.aidea.vibefiction.entity.Chapters;

import java.util.List;

public interface ChaptersRepositoryCustom {

    List<ChapterResponseSH> findAllChaptersAndAuthorNameByNovelId(Long novelId);

}
