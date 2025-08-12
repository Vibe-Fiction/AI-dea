package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Proposals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalsRepository extends JpaRepository<Proposals, Long> {
    List<Proposals>
    findByChapterIdOrderByCreatedAtAsc(Long chapterId);
}
