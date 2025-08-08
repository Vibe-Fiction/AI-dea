package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}
