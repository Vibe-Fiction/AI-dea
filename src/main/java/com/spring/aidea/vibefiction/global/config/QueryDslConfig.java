package com.spring.aidea.vibefiction.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    /**
     * QueryDSL 설정 클래스
     * JPAQueryFactory를 Spring Bean으로 등록하여 QueryDSL 사용을 위한 설정
     */
    @Configuration
    @RequiredArgsConstructor
    public class QueryDslConfig {

        private final EntityManager entityManager;

        /**
         * JPAQueryFactory Bean 등록
         * QueryDSL을 사용하기 위한 핵심 컴포넌트
         */
        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

