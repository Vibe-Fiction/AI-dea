package com.spring.aidea.vibefiction.dto.request.novel;

import com.spring.aidea.vibefiction.entity.Novels.NovelVisibility;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 새로운 소설 작품을 생성하기 위해 사용되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 소설의 기본 정보(제목, 시놉시스, 장르 등)와 함께 첫 번째 회차(1화)의
 * 제목과 내용을 담아, 소설과 1화를 한 번의 요청으로 동시에 생성하는 기능을 지원합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
//@Setter // [보안/설계] 불변성을 위해 Setter는 비활성화. 생성 시에만 값이 할당되도록 Builder 패턴 사용을 권장.
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
    @NotBlank(message = "소설 제목은 비어 있을 수 없습니다.")
    @Size(max = 50, message = "소설 제목은 50자를 초과할 수 없습니다.")
    private String title;

    /**
     * 소설의 전체 줄거리를 요약한 시놉시스입니다. (선택 사항)
     * <p>
     * <b>[비즈니스 규칙]</b> 독자들이 소설을 읽기 전 작품의 내용을 파악하는 데 도움을 줍니다.
     * 최대 2000자까지 입력 가능합니다.
     */
    @Size(max = 2000, message = "시놉시스는 2000자를 초과할 수 없습니다.")
    private String synopsis;

    /**
     * 소설 생성과 동시에 등록될 첫 번째 회차(1화)의 제목입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설 생성 시 1화를 함께 등록하는 편의 기능을 위해 사용됩니다.
     * 비어 있을 수 없습니다.
     */
    @NotBlank(message = "1화 제목은 비어 있을 수 없습니다.")
    @Size(max = 60, message = "1화 제목은 60자를 초과할 수 없습니다.")
    private String firstChapterTitle;

    /**
     * 소설 생성과 동시에 등록될 첫 번째 회차(1화)의 본문 내용입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설의 시작을 알리는 내용으로, 비어 있을 수 없습니다.
     * 최대 5000자까지 입력 가능합니다.
     */
    @NotBlank(message = "1화 내용은 비어 있을 수 없습니다.")
    @Size(max = 5000, message = "1화 내용은 5000자를 초과할 수 없습니다.")
    private String firstChapterContent;

    /**
     * 소설에 적용할 장르의 고유 ID 목록입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 소설의 카테고리 분류 및 검색에 사용되며,
     * 최소 1개 이상의 장르를 필수로 선택해야 합니다.
     *
     * @see com.spring.aidea.vibefiction.entity.Genres.Genre
     */
    @NotEmpty(message = "장르는 최소 1개 이상 선택해야 합니다.")
    private List<Integer> genreIds;

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
