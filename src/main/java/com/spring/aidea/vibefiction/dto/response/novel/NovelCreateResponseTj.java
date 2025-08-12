package com.spring.aidea.vibefiction.dto.response.novel;

import lombok.*;

/** [TJ] 새 소설 생성 응답 DTO */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NovelCreateResponseTj {
    private Long novelId;
    private Long firstChapterId; // 1화 동시 생성 시에만 값 존재
}
