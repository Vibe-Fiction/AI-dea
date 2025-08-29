package com.spring.aidea.bsh;


import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MainPageTest {


    @Autowired
    NovelsRepository novelsRepository;
    @Autowired
    NovelGenresRepository novelGenresRepository;
    @Autowired
    GenresRepository genresRepository;
    @Autowired
    CollaboratorsRepository collaboratorsRepository;
    @Autowired
    FavoritesRepository favoritesRepository;
    @Autowired
    ChaptersRepository chaptersRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    ProposalsRepository proposalsRepository;
    @Autowired
    VotesRepository votesRepository;
    @Autowired
    AiInteractionLogsRepository aiInteractionLogsRepository;


    @Test
    @DisplayName("소설 전체 조회")
    void findAllNovels() {
        //given

        //when
        List<Novels> novels = novelsRepository.findAll();
        //then
        novels.forEach(System.out::println);
    }

    @Test
    @DisplayName("소설 단건 조회")
    void findNovel() {
        //given
        Long novelId = 1L;
        //when
        Novels novel = novelsRepository.findById(novelId).orElse(null);
        //then
        assertNotNull(novel, "찾는 소설이 없습니다.");
        System.out.println(novel);
    }



}
