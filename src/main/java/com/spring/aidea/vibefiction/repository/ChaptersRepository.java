package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Chapters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChaptersRepository extends JpaRepository<Chapters, Long> {
    Optional<Chapters>
    findTop1ByNovelIdOrderByChapterNumberDesc(Long novelId);
}
