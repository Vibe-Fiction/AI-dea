package com.spring.aidea.vibefiction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.AiContinueRequestTj;
import com.spring.aidea.vibefiction.dto.request.aiInteractionLog.AiRecommendNovelRequestTj;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.AiContinueResponseTj;
import com.spring.aidea.vibefiction.dto.response.aiInteractionLog.AiRecommendNovelResponseTj;
import com.spring.aidea.vibefiction.entity.AiInteractionLogs;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Genres;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.AiInteractionLogsRepository;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 기반의 보조 기능(소설 추천, 이어쓰기 제안)을 처리하는 핵심 서비스 클래스입니다.
 * <p>
 * 이 서비스는 AI 모델과의 상호작용을 담당하며, 프롬프트 생성, AI 응답 파싱,
 * 그리고 모든 상호작용에 대한 로그를 데이터베이스에 기록하는 책임을 가집니다.
 * <p>
 * <b>[설계]</b> AI 서비스와의 의존성을 추상적인 {@link GeminiApiService} 인터페이스로 설정하여,
 * 향후 다른 AI 서비스로의 교체가 용이하도록 유연한 구조로 설계되었습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistServiceTj {

    /** 실제 AI API 호출을 담당하는 서비스 인터페이스입니다. DI(의존성 주입)를 통해 실제 구현체(GeminiApiServiceImpl)가 주입됩니다. */
    private final GeminiApiService geminiApiService;
    /** 사용자와 AI의 모든 상호작용을 데이터베이스에 기록하여, 사용량 분석이나 문제 추적에 활용하기 위한 저장소입니다. */
    private final AiInteractionLogsRepository aiInteractionLogsRepository;
    /** AI 이어쓰기 시, 이전 회차 내용을 조회하기 위해 사용되는 저장소입니다. */
    private final ChaptersRepository chaptersRepository;
    /** 요청의 주체인 사용자를 식별하고 AI 로그에 기록하기 위해 사용되는 저장소입니다. */
    private final UsersRepository usersRepository;

    /**
     * 사용자가 입력한 장르와 시놉시스를 기반으로 AI에게 새로운 소설의 제목과 1화 내용을 추천받습니다.
     * <p>
     * AI의 성능을 극대화하기 위해, 역할, 지시사항, 제약 조건 등을 명시하는
     * 정교한 프롬프트 엔지니어링 기법이 적용되었습니다.
     *
     * @param userId AI 추천을 요청한 사용자의 고유 ID.
     * @param req    추천에 필요한 소설 장르(genre)와 시놉시스(synopsis)를 담은 DTO.
     * @return AI가 추천한 제목과 내용을 담은 {@link AiRecommendNovelResponseTj}.
     * @throws RuntimeException AI 서비스 호출 또는 응답 파싱에 실패한 경우.
     */
    @Transactional
    public AiRecommendNovelResponseTj recommendForNewNovel(Long userId, AiRecommendNovelRequestTj req) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // [리팩토링] AI가 JSON 형식 오류를 자주 반환하는 문제를 해결하기 위해, 텍스트와 구분자를 사용하도록 프롬프트 변경
        String prompt = String.format(
            """
            ## ROLE & GOAL
            당신은 Vibe Fiction 플랫폼을 위한 창의적인 웹소설 작가 AI입니다. 당신의 목표는 사용자가 제공한 최소한의 정보(장르, 시놉시스)를 바탕으로, 즉시 독자들의 시선을 사로잡을 수 있는 매력적인 소설의 시작을 제안하는 것입니다.

            ## INSTRUCTIONS
            1.  **Analyze Input**: 주어진 '장르'와 '시놉시스'를 분석하여 핵심 키워드와 분위기를 파악하세요.
            2.  **Generate Creatively**: 분석한 내용을 바탕으로 소설 제목, 1화 제목, 1화 내용을 생성하세요.
            3.  **Leave Open-ended**: 릴레이 소설의 '첫 화'이므로, 다음 작가가 이야기를 이어갈 수 있도록 열린 결말로 마무리하세요.
            4.  **Adhere to Constraints**: 생성하는 모든 텍스트는 아래의 '길이 제한' 규칙을 반드시 준수해야 합니다.
            5.  **Format Output**: **매우 중요합니다.** 아래 설명된 텍스트 형식과 구분자를 반드시 지켜서 답변해야 합니다. JSON 형식을 사용하지 마세요.

            ## INPUT DATA
            -   **장르**: "%s"
            -   **시놉시스**: "%s"

            ## CONSTRAINTS (길이 제한)
            -   **novelTitle**: 최대 50자
            -   **firstChapterTitle**: 최대 60자
            -   **firstChapterContent**: 최소 200자, 최대 5000자

            ## OUTPUT FORMAT (TEXT ONLY, USE SEPARATOR)
            -   첫 번째 줄: 생성된 소설 제목
            -   두 번째 줄: --- (하이픈 3개 구분자)
            -   세 번째 줄: 생성된 1화 제목
            -   네 번째 줄: --- (하이픈 3개 구분자)
            -   다섯 번째 줄부터: 생성된 1화 내용

            ## 예시:
            악녀는 조용히 살고 싶을 뿐
            ---
            제1화: 내가 왜 여기에?
            ---
            차가운 대리석 바닥의 감촉에 정신이 들었다...
            """,
            req.getGenre(), req.getSynopsis()
        );

        log.info("Gemini API에 소설 추천을 요청합니다. (사용자 ID: {})", userId);
        String aiResultText = geminiApiService.generateContent(prompt);

        AiInteractionLogs logEntity = AiInteractionLogs.builder()
            .user(user)
            .type(AiInteractionLogs.AiInteractionType.NOVEL_CREATION)
            .prompt(prompt)
            .result(aiResultText)
            .build();
        aiInteractionLogsRepository.save(logEntity);
        log.info("AI 상호작용 로그를 저장했습니다. (로그 ID: {})", logEntity.getLogId());

        try {
            // [리팩토링] JSON 파싱 대신, 구분자("---") 기반의 텍스트 파싱 로직으로 변경하여 안정성 확보
            String[] parts = aiResultText.split("\n---\n", 3);

            String novelTitle = (parts.length > 0) ? parts[0].trim() : "제목 추천 실패";
            String firstChapterTitle = (parts.length > 1) ? parts[1].trim() : "1화 제목 추천 실패";
            String firstChapterContent = (parts.length > 2) ? parts[2].trim() : "1화 내용 추천 실패";

            return AiRecommendNovelResponseTj.builder()
                .logId(logEntity.getLogId())
                .novelTitle(novelTitle)
                .firstChapterTitle(firstChapterTitle)
                .firstChapterContent(firstChapterContent)
                .build();
        } catch (Exception e) {
            log.error("AI 응답(텍스트) 파싱 실패. AI로부터 받은 원본 응답: {}", aiResultText, e);
            throw new RuntimeException("AI 응답을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public AiContinueResponseTj continueForChapter(Long userId, Long chapterId, AiContinueRequestTj req) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        Chapters baseChapter = chaptersRepository.findById(chapterId)
            .orElseThrow(() -> new IllegalArgumentException("기반 회차를 찾을 수 없습니다. ID: " + chapterId));

        String fullStoryContext = buildFullStoryContext(baseChapter);

        String prompt = String.format(
            """
            ## ROLE & GOAL
            당신은 Vibe Fiction 플랫폼을 위한 전문 웹소설 AI 어시스턴트입니다. 당신의 임무는 주어진 소설의 전체 맥락과 사용자의 새로운 요구사항을 깊이 이해하여, 다음 회차의 초안을 일관성 있고 창의적으로 작성하는 것입니다.

            ## INSTRUCTIONS
            1.  **Strictly Continue the Story**: **가장 중요한 규칙입니다.** '이전 회차 전체 내용'의 마지막 문장에서 이야기가 바로 이어지도록 다음 내용을 작성해야 합니다.
            2.  **Maintain All Details**: 등장인물의 이름, 능력 등 '이전 회차 전체 내용'에 언급된 모든 세부 설정을 변경하거나 무시해서는 안 됩니다.
            3.  **Incorporate User Request**: '사용자의 추가 요구사항'을 기존 설정과 충돌하지 않는 선에서 다음 이야기의 핵심 사건으로 자연스럽게 녹여내세요.
            4.  **Leave Open-ended**: 이 이야기는 계속 이어져야 하므로, 반드시 열린 결말(Open-ended)로 마무리해야 합니다.
            5.  **Adhere to Constraints**: 생성하는 모든 텍스트는 아래의 '길이 제한' 규칙을 반드시 준수해야 합니다.
            6.  **Format Output**: **매우 중요합니다.** 아래 설명된 텍스트 형식과 구분자를 반드시 지켜서 답변해야 합니다. JSON 형식을 사용하지 마세요.

            ## CONTEXT
            ### 소설 기본 정보:
            -   제목: "%s", 장르: [%s], 시놉시스: "%s"
            ### 이전 회차 전체 내용:
            %s

            ## USER REQUEST
            -   다음 이야기 요구사항: "%s"

            ## CONSTRAINTS (길이 제한)
            -   suggestedTitle: 최대 60자
            -   suggestedContent: 최대 5000자

            ## OUTPUT FORMAT (TEXT ONLY, USE SEPARATOR)
            -   첫 줄: 생성된 다음 회차의 제목
            -   두 번째 줄: --- (하이픈 3개 구분자)
            -   세 번째 줄부터: 생성된 다음 회차의 내용
            """,
            baseChapter.getNovel().getTitle(),
            baseChapter.getNovel().getNovelGenres().stream().map(ng -> ng.getGenre().getName().getDescription()).collect(Collectors.joining(", ")),
            baseChapter.getNovel().getSynopsis(),
            fullStoryContext,
            req.getInstruction()
        );

        log.info("Gemini API에 이어쓰기 추천을 요청합니다. (사용자 ID: {}, 챕터 ID: {})", userId, chapterId);
        String aiResultText = geminiApiService.generateContent(prompt);

        AiInteractionLogs logEntity = AiInteractionLogs.builder()
            .user(user)
            .type(AiInteractionLogs.AiInteractionType.PROPOSAL_GENERATION)
            .prompt(prompt)
            .result(aiResultText)
            .basedOnChapter(baseChapter)
            .build();
        aiInteractionLogsRepository.save(logEntity);
        log.info("AI 상호작용 로그를 저장했습니다. (로그 ID: {})", logEntity.getLogId());

        try {
            // [리팩토링] JSON 파싱 대신, 구분자("---") 기반의 텍스트 파싱 로직으로 변경하여 안정성 확보
            String[] parts = aiResultText.split("\n---\n", 2);

            String title = (parts.length > 0) ? parts[0].trim() : "제목 제안 실패";
            String content = (parts.length > 1) ? parts[1].trim() : "내용 제안 실패";

            return AiContinueResponseTj.builder()
                .logId(logEntity.getLogId())
                .suggestedTitle(title)
                .suggestedContent(content)
                .build();
        } catch (Exception e) {
            log.error("AI 응답(텍스트) 파싱 실패. AI로부터 받은 원본 응답: {}", aiResultText, e);
            throw new RuntimeException("AI 응답을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * AI에게 일관된 스토리 컨텍스트를 제공하기 위해, 특정 회차까지의 모든 내용을 하나의 문자열로 조합하는 헬퍼 메서드입니다.
     * <p>
     * 소설의 모든 회차를 조회하여 회차 번호 순으로 정렬한 뒤, 정해진 형식으로 포맷팅하여
     * AI가 이전 내용을 쉽게 파악할 수 있도록 돕습니다.
     *
     * @param baseChapter 컨텍스트를 구성할 기준이 되는 회차 (이 회차의 부모 소설을 통해 전체 회차를 조회).
     * @return 회차별 제목과 내용이 구분되어 연결된 전체 소설 텍스트.
     */
    private String buildFullStoryContext(Chapters baseChapter) {
        List<Chapters> allChapters = baseChapter.getNovel().getChapters();
        return allChapters.stream()
            .sorted((c1, c2) -> c1.getChapterNumber().compareTo(c2.getChapterNumber()))
            .map(c -> String.format("제%d화: %s\n%s", c.getChapterNumber(), c.getTitle(), c.getContent()))
            .collect(Collectors.joining("\n\n---\n\n"));
    }
}
