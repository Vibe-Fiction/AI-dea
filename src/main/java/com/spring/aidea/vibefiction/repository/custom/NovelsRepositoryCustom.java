package com.spring.aidea.vibefiction.repository.custom;

import com.spring.aidea.vibefiction.entity.Novels;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NovelsRepositoryCustom {


    // 소설 전체조회 (페이징 처리)
    List<Novels> findAllNovelsPage(Pageable pageable);
    // 작가 닉네임으로 소설 조회
    List<Novels> findNovelsByAuthorId(Long authorId);

}
