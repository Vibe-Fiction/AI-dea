package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotesRepository extends JpaRepository<Votes, Long> {
}
