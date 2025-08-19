package com.spring.aidea.vibefiction.dto.response.genre;

import com.spring.aidea.vibefiction.entity.Genres;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 장르 정보를 클라이언트에 전달하기 위한 데이터 전송 객체(DTO)입니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@AllArgsConstructor
public class GenreResponse {

    /**
     * 장르의 Enum 상수명입니다. (예: "FANTASY", "ROMANCE")
     * 클라이언트와 서버 간의 데이터 교환 시 사용되는 고유 식별자 역할을 합니다.
     */
    private String code;

    /**
     * 사용자에게 보여질 장르의 한글 설명입니다. (예: "판타지", "로맨스")
     */
    private String description;

    /**
     * {@link com.spring.aidea.vibefiction.entity.Genres.GenreType} Enum을
     * 이 DTO 객체로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param type 변환할 GenreType Enum 상수.
     * @return 변환된 GenreResponse 객체.
     */
    public static GenreResponse fromEnum(Genres.GenreType type) {
        return new GenreResponse(type.name(), type.getDescription());
    }
}
