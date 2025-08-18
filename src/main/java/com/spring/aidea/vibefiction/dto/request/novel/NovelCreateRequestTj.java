package com.spring.aidea.vibefiction.dto.request.novel;

import com.spring.aidea.vibefiction.entity.Novels.NovelVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 새로운 소설 작품을 생성하기 위해 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * <p>이 객체는 소설의 기본 정보(제목, 시놉시스, 장르 등)와 함께 첫 번째 회차(1화)의
 * 제목과 내용을 담아, 소설과 1화를 한 번의 요청으로 동시에 생성하는 기능을 지원합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
//@Setter // [보안/설계] 불변성을 위해 Setter는 비활성화.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelCreateRequestTj {

    /**
     * 새로 생성할 소설의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설을 식별하는 가장 중요한 정보이며, 비어 있을 수 없습니다.
     * 최대 50자까지 입력 가능합니다.
     */
    @NotBlank(message = "소설 제목은 비워 있을 수 없습니다.")
    @Size(max = 50, message = "소설 제목은 50자를 초과할 수 없습니다.")
    private String title;

    /**
     * [리팩토링] 사용자가 구상하고 있는 소설의 핵심 아이디어나 줄거리입니다. (필수 입력)
     * <p>
     * <b>[비즈니스 규칙]</b> 독자들이 소설을 선택하기 전 작품의 대략적인 내용을 파악하는 데 도움을 주며,
     * AI 보조 기능 사용 시 더 정확한 추천을 생성하는 기반 데이터로 활용됩니다.
     * 최대 2000자까지 입력 가능합니다.
     */
    @NotBlank(message = "시놉시스는 비워둘 수 없습니다.")
    @Size(max = 2000, message = "시놉시스는 2000자를 초과할 수 없습니다.")
    private String synopsis;

    /**
     * 소설 생성과 동시에 등록될 첫 번째 회차(1화)의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설 생성 시 1화를 함께 등록하는 편의 기능을 위해 사용됩니다.
     * 비어 있을 수 없습니다.
     */
    @NotBlank(message = "1화 제목은 비워 있을 수 없습니다.")
    @Size(max = 60, message = "1화 제목은 60자를 초과할 수 없습니다.")
    private String firstChapterTitle;

    /**
     * 소설 생성과 동시에 등록될 첫 번째 회차(1화)의 본문 내용입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설의 시작을 알리는 내용으로, 비어 있을 수 없습니다.
     * 최대 5000자까지 입력 가능합니다.
     */
    @NotBlank(message = "1화 내용은 비워 있을 수 없습니다.")
    @Size(max = 5000, message = "1화 내용은 5000자를 초과할 수 없습니다.")
    private String firstChapterContent;

    /*
     * [리팩토링-AS-IS] 기존 장르 입력 방식 (DB의 genre_id 기반)
     * @author 왕택준
     *
     * [주석 처리 이유]
     * Genres 엔티티의 name 필드가 GenreType Enum으로 변경됨에 따라, 클라이언트와의 통신 방식도
     * DB의 PK(ID)가 아닌, Enum의 상수명(e.g., "FANTASY")을 문자열로 직접 주고받도록 수정합니다.
     * 이는 클라이언트가 장르 ID를 미리 알 필요 없이, 약속된 문자열만으로 통신할 수 있게 하여
     * 시스템 간의 결합도를 낮추는 효과가 있습니다.
     *
    @NotEmpty(message = "장르는 최소 1개 이상 선택해야 합니다.")
    private List<Integer> genreIds;
    */

    /**
     * [리팩토링-TO-BE] 소설에 적용할 장르의 이름(Enum 상수명) 목록입니다.
     * <p>
     * <b>[전송 예시]</b>: {@code ["FANTASY", "ROMANCE"]}
     * <p>
     * <b>[비즈니스 규칙]</b> 소설의 카테고리 분류 및 검색에 사용되며,
     * 최소 1개 이상의 장르를 필수로 선택해야 합니다.
     *
     * @see com.spring.aidea.vibefiction.entity.Genres.GenreType
     */
    @NotEmpty(message = "장르는 최소 1개 이상 선택해야 합니다.")
    private List<String> genres;

    /**
     * 소설의 공개 범위 설정 값입니다. (PUBLIC, PRIVATE, FRIENDS 중 하나)
     * <p>
     * <b>[비즈니스 규칙]</b> 소설의 접근 제어 수준을 결정하며, 반드시 설정되어야 합니다.
     * <ul>
     *  <li>{@code PUBLIC}: 전체 공개</li>
     *  <li>{@code PRIVATE}: 비공개 (작성자 및 특정 사용자만 접근 가능)</li>
     *  <li>{@code FRIENDS}: 친구 공개</li>
     * </ul>
     */
    @NotNull(message = "공개 범위를 설정해야 합니다.")
    private NovelVisibility visibility;

    /**
     * 소설 접근 시 필요한 비밀번호입니다. (선택 사항)
     * <p>
     * <b>[비즈니스 규칙]</b> 공개 범위(visibility)가 {@code PRIVATE}으로 설정된 경우,
     * 특정 독자에게만 접근을 허용하기 위한 비밀번호로 사용될 수 있습니다.
     */
    private String accessPassword;
}
