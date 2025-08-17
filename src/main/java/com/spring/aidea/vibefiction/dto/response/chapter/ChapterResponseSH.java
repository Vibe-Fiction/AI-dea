package com.spring.aidea.vibefiction.dto.response.chapter;


import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@Data
public class ChapterResponseSH {
//     .select(
//        chapters.chapterId,
//        chapters.novel.novelId,
//        chapters.chapterNumber,
//        chapters.title,
//        chapters.content,
//        users.nickname.coalesce(users.nickname) // NVL을 coalesce로 변환
//            )


    private Long chapterId;
    private Long novelId;
    private Integer chapterNumber;
    private String title;
    private String content;
    private String author;


    @QueryProjection
    public ChapterResponseSH(
        Long chapterId, Long novelId, Integer chapterNumber,
        String title, String content, String author) {

        this.chapterId = chapterId;
        this.novelId = novelId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
