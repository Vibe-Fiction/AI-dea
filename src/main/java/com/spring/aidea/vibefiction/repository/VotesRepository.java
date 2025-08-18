package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.entity.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VotesRepository extends JpaRepository<Votes, Long> {
    boolean existsByUserAndProposal(Users user, Proposals proposal);
    List<Votes> findByEndTimeBeforeAndStatus(LocalDateTime endTime, Chapters.Status status);
}
