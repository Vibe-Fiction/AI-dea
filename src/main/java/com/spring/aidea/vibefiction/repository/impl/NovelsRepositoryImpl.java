package com.spring.aidea.vibefiction.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.QNovels;
import com.spring.aidea.vibefiction.repository.custom.NovelsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NovelsRepositoryImpl implements NovelsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Novels> findAllNovelsPage(Pageable pageable) {
        QNovels novels = QNovels.novels;

        return queryFactory
            .selectFrom(novels) // QNovels에서 모든 컬럼을 조회
            .orderBy(novels.createdAt.desc()) // 최신순으로 정렬 (예시)
            .offset(pageable.getOffset()) // 페이지 시작 위치 (0부터 시작)
            .limit(pageable.getPageSize()) // 페이지 당 항목 수 (8개)
            .fetch(); // 쿼리 실행 및 결과 반환
    }

}
