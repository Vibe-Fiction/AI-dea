package com.spring.aidea.vibefiction.repository.custom;

import com.spring.aidea.vibefiction.entity.Novels;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NovelsRepositoryCustom {


    List<Novels> findAllNovelsPage(Pageable pageable);
}
