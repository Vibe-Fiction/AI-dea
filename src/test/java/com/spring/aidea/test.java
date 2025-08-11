package com.spring.aidea;

import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class test {

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


}
