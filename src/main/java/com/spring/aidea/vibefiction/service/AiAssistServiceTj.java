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
    /** AI가 반환하는 JSON 형식의 응답 문자열을 자바 객체(JsonNode)로 파싱하기 위해 사용됩니다. */
    private final ObjectMapper objectMapper;

    /**
     * 사용자가 입력한 장르와 시놉시스를 기반으로 AI에게 새로운 소설의 제목과 1화 내용을 추천받습니다.
     * <p>
     * 이 메서드는 AI 모델에 전달할 프롬프트를 생성하고, AI의 응답(JSON)을 파싱하여 클라이언트가 사용하기 쉬운
     * DTO 형태로 가공하여 반환합니다. 모든 AI 상호작용 과정은 {@link AiInteractionLogs}에 기록됩니다.
     *
     * @param userId AI 추천을 요청한 사용자의 고유 ID.
     * @param req    추천에 필요한 소설 장르(genre)와 시놉시스(synopsis)를 담은 DTO.
     * @return AI가 추천한 제목과 내용을 담은 {@link AiRecommendNovelResponseTj}.
     * @throws IllegalArgumentException 요청한 ID에 해당하는 사용자가 존재하지 않을 경우.
     * @throws RuntimeException         AI 서비스 호출 또는 응답 파싱에 실패한 경우.
     */
    @Transactional
    public AiRecommendNovelResponseTj recommendForNewNovel(Long userId, AiRecommendNovelRequestTj req) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        /*
         * [리팩토링-AS-IS] 기존 프롬프트
         *
         * [주석 처리 이유] by 왕택준
         * 기존 프롬프트는 AI에게 기본적인 정보만 제공하여 결과물의 일관성과 품질을 보장하기 어려웠습니다.
         * 아래 TO-BE 프롬프트에서는 역할(Role), 명확한 지시사항(Instructions), 구조화된 입력 데이터(Input Data) 등을
         * 명시하는 프롬프트 엔지니어링 기법을 적용하여 AI가 의도를 더 잘 파악하고 고품질의 JSON 응답을
         * 생성하도록 개선했습니다.
         *
        String prompt = String.format(
            \"\"\"
            당신은 웹소설 전문 작가입니다. ...
            { ... }
            \"\"\",
            req.getGenre(), req.getSynopsis()
        );
        */

        // [리팩토링-TO-BE] 강화된 프롬프트
        // [프롬프트 강화] 역할, 지시사항, 컨텍스트, 출력 형식 등을 명확히 구분하여 AI의 성능을 극대화
        String prompt = String.format(
            """
            ## ROLE & GOAL
            당신은 Vibe Fiction 플랫폼을 위한 창의적인 웹소설 작가 AI입니다. 당신의 목표는 사용자가 제공한 최소한의 정보(장르, 시놉시스)를 바탕으로, 즉시 독자들의 시선을 사로잡을 수 있는 매력적인 소설의 시작을 제안하는 것입니다.

            ## INSTRUCTIONS
            1.  **Analyze Input**: 주어진 '장르'와 '시놉시스'를 분석하여 핵심 키워드와 분위기를 파악하세요.
            2.  **Generate Novel Title**: 분석한 내용을 바탕으로, 소설의 전체 내용을 암시하면서도 호기심을 유발하는 소설 제목을 생성하세요.
            3.  **Generate Chapter 1 Title**: 소설의 시작을 알리는 첫 번째 회차의 제목을 생성하세요. "1화" 또는 "프롤로그"와 같은 형식을 포함해도 좋습니다.
            4.  **Generate Chapter 1 Content**: 시놉시스를 바탕으로, 이야기의 배경을 설정하고, 주인공을 소개하며, 독자들이 다음 화를 궁금해할 만한 핵심 사건이나 복선을 암시하는 도입부를 200~300자 내외로 작성하세요.
            5.  **Format Output**: 결과는 반드시 JSON 형식으로, JSON 코드 블록 없이 순수한 텍스트로만 제공해야 합니다.

            ## INPUT DATA
            -   **장르**: "%s"
            -   **시놉시스**: "%s"

            ## OUTPUT FORMAT (JSON ONLY)
            {
              "novelTitle": "생성된 소설 제목",
              "firstChapterTitle": "생성된 1화 제목",
              "firstChapterContent": "생성된 1화 내용"
            }
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
            String pureJson = extractJsonFromString(aiResultText);
            JsonNode finalJson = objectMapper.readTree(pureJson);

            return AiRecommendNovelResponseTj.builder()
                .logId(logEntity.getLogId())
                .novelTitle(finalJson.path("novelTitle").asText("제목 추천 실패"))
                .firstChapterTitle(finalJson.path("firstChapterTitle").asText("1화 제목 추천 실패"))
                .firstChapterContent(finalJson.path("firstChapterContent").asText("1화 내용 추천 실패"))
                .build();
        } catch (JsonProcessingException e) {
            log.error("AI 응답(JSON) 파싱 실패. AI로부터 받은 원본 응답: {}", aiResultText, e);
            throw new RuntimeException("AI가 유효하지 않은 형식의 응답을 반환했습니다.", e);
        }
    }

    /**
     * 기존 소설의 특정 회차 내용을 기반으로, AI에게 이어질 다음 이야기를 제안받습니다.
     * <p>
     * 이전 회차까지의 전체 내용을 컨텍스트로 제공하고 사용자의 추가 요구사항을 반영하여,
     * 일관성 있고 자연스러운 다음 회차의 제목과 내용을 추천받습니다.
     * 모든 AI 상호작용 과정은 {@link AiInteractionLogs}에 기록됩니다.
     *
     * @param userId    이어쓰기를 요청한 사용자의 고유 ID.
     * @param chapterId 이어쓰기의 기준이 될 회차의 고유 ID.
     * @param req       이어쓰기에 대한 추가적인 지시사항을 담은 DTO.
     * @return AI가 제안한 제목과 내용을 담은 {@link AiContinueResponseTj}.
     * @throws IllegalArgumentException 요청한 ID의 사용자 또는 회차가 존재하지 않을 경우.
     * @throws RuntimeException         AI 서비스 호출 또는 응답 파싱에 실패한 경우.
     */
    @Transactional
    public AiContinueResponseTj continueForChapter(Long userId, Long chapterId, AiContinueRequestTj req) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        Chapters baseChapter = chaptersRepository.findById(chapterId)
            .orElseThrow(() -> new IllegalArgumentException("기반 회차를 찾을 수 없습니다. ID: " + chapterId));

        String fullStoryContext = buildFullStoryContext(baseChapter);

        /*
         * [리팩토링-AS-IS] 기존 프롬프트
         *
         * [주석 처리 이유] by 왕택준
         * 기존 프롬프트는 '이전 회차 내용'과 '사용자 요구사항'만 제공하여 AI가 소설의 전체적인 맥락
         * (제목, 장르, 시놉시스)을 파악하기 어려웠습니다. 아래 TO-BE 프롬프트에서는 CONTEXT 섹션을 추가하여
         * 소설의 기본 정보까지 함께 제공함으로써, AI가 더 일관성 있고 맥락에 맞는 이야기를 생성하도록 개선했습니다.
         *
        String prompt = String.format(
            \"\"\"
            당신은 프로 웹소설 작가입니다. ...
            --- 전체 소설 내용 ---
            %s
            --- 사용자의 추가 요구사항 ---
            "%s"
            ...
            \"\"\",
            fullStoryContext,
            req.getInstruction()
        );
        */

        // [리팩토링-TO-BE] 강화된 프롬프트
        // [프롬프트 강화] 이어쓰기에 필요한 모든 정보(소설 정보, 이전 내용, 사용자 요구)를 구조화하여 제공
        String prompt = String.format(
            """
            ## ROLE & GOAL
            당신은 Vibe Fiction 플랫폼을 위한 전문 웹소설 AI 어시스턴트입니다. 당신의 임무는 주어진 소설의 전체 맥락과 사용자의 새로운 요구사항을 깊이 이해하여, 다음 회차의 초안을 일관성 있고 창의적으로 작성하는 것입니다.

            ## INSTRUCTIONS
            1.  **Analyze Context**: 아래에 제공되는 '소설 기본 정보'와 '이전 회차 전체 내용'을 완벽하게 분석하여, 소설의 장르, 분위기, 문체, 인물들의 성격과 관계, 그리고 현재까지의 사건 흐름을 파악하세요.
            2.  **Incorporate User Request**: '사용자의 추가 요구사항'을 다음 이야기의 핵심 사건으로 삼아 자연스럽게 전개하세요.
            3.  **Maintain Consistency**: 이전 내용과 충돌하는 설정 오류(e.g., 죽은 인물의 재등장)나 급격한 문체 변화를 피하고, 이야기의 일관성을 반드시 유지해야 합니다.
            4.  **Generate Creatively**: 단순히 내용을 잇는 것을 넘어, 독자들이 흥미를 느낄 만한 긴장감, 복선, 또는 감정적인 묘사를 추가하여 글의 품질을 높여주세요.
            5.  **Format Output**: 결과는 반드시 JSON 형식으로, JSON 코드 블록 없이 순수한 텍스트로만 제공해야 합니다.

            ## CONTEXT
            ### 소설 기본 정보:
            -   **제목**: "%s"
            -   **장르**: [%s]
            -   **시놉시스**: "%s"

            ### 이전 회차 전체 내용 (1화부터 순서대로):
            %s

            ## USER REQUEST
            -   **다음 이야기 요구사항**: "%s"

            ## OUTPUT FORMAT (JSON ONLY)
            {
              "suggestedTitle": "생성된 다음 회차의 흥미로운 제목",
              "suggestedContent": "사용자 요구사항이 반영된, 약 300~500자 내외의 몰입감 있는 다음 회차 내용 초안."
            }
            """,
            baseChapter.getNovel().getTitle(),
            baseChapter.getNovel().getNovelGenres().stream()
                .map(novelGenre -> novelGenre.getGenre().getName().getDescription())
                .collect(Collectors.joining(", ")),
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
            String pureJson = extractJsonFromString(aiResultText);
            JsonNode finalJson = objectMapper.readTree(pureJson);

            return AiContinueResponseTj.builder()
                .logId(logEntity.getLogId())
                .suggestedTitle(finalJson.path("suggestedTitle").asText("제목 제안 실패"))
                .suggestedContent(finalJson.path("suggestedContent").asText("내용 제안 실패"))
                .build();
        } catch (JsonProcessingException e) {
            log.error("AI 응답(JSON) 파싱 실패. AI로부터 받은 원본 응답: {}", aiResultText, e);
            throw new RuntimeException("AI가 유효하지 않은 형식의 응답을 반환했습니다.", e);
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

    /**
     * AI가 반환한 전체 문자열에서 순수한 JSON 부분만 추출하는 헬퍼 메서드입니다.
     * <p>
     * Gemini와 같은 LLM은 종종 응답에 "```json\n{...}\n```" 와 같은 마크다운 코드 블록을
     * 포함할 수 있습니다. 이 메서드는 이러한 추가 텍스트를 제거하고 안정적으로 JSON만 파싱할 수 있도록 합니다.
     *
     * @param text AI의 전체 응답 문자열
     * @return JSON 형식의 문자열
     */
    private String extractJsonFromString(String text) {
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
            return text.substring(firstBrace, lastBrace + 1);
        }
        log.warn("AI 응답에서 JSON 형식을 찾지 못했습니다. 원본 텍스트를 그대로 반환합니다: {}", text);
        return text;
    }
}
