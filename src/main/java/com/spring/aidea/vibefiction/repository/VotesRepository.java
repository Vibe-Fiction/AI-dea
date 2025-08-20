package com.spring.aidea.vibefiction.repository;


import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.entity.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VotesRepository extends JpaRepository<Votes, Long> {
    boolean existsByUserAndProposal(Users user, Proposals proposal);


    boolean existsByUser_UserIdAndProposal_Chapter_ChapterId(Long userId, Long chapterId);

    Optional<Votes> findByUserAndProposal(Users user, Proposals proposal);
}
