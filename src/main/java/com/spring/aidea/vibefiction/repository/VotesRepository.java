package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Votes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotesRepository extends JpaRepository<Votes, Long> {
}
