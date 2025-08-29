package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Collaborators;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollaboratorsRepository extends JpaRepository<Collaborators, Long> {


    @EntityGraph(attributePaths = {"user","novel"})
    @Query("select distinct c from Collaborators c")
    List<Collaborators> findCollaboUserNameByNovelId(Long novelId);


}
