package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Genres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenresRepository extends JpaRepository<Genres, Integer> {

}
