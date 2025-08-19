package com.spring.aidea.vibefiction.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterResponseSH;
import com.spring.aidea.vibefiction.dto.response.chapter.QChapterResponseSH;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.QChapters;
import com.spring.aidea.vibefiction.entity.QUsers;
import com.spring.aidea.vibefiction.repository.custom.ChaptersRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChaptersRepositoryImpl implements ChaptersRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChapterResponseSH> findAllChaptersAndAuthorNameByNovelId(Long novelId) {

        QChapters chapters = QChapters.chapters;
        QUsers users = QUsers.users;


        return queryFactory
            .select(new QChapterResponseSH( // DTO의 생성자를 직접 호출하여 Projection
                chapters.chapterId,
                chapters.novel.novelId,
                chapters.chapterNumber,
                chapters.title,
                chapters.content,
                users.nickname // 작성자 닉네임
            ))
            .from(chapters)
            // chapters 엔티티와 연관된 author(Users)를 기준으로 join 합니다.
            .leftJoin(chapters.author, users)
            .where(chapters.novel.novelId.eq(novelId))
            .fetch();

    }
}
