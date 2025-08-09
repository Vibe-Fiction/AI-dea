package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Novels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface NovelsRepository extends JpaRepository<Novels, Long> {


}
