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
 * <b>[리팩토링]</b> AI 서비스와의 의존성을 구체적인 구현체({@code MockAIServiceTj})가 아닌,
 * 추상적인 {@link GeminiApiService} 인터페이스로 변경하여, 향후 다른 AI 서비스로의 교체가 용이하도록
 * 유연한 구조로 개선되었습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistServiceTj {

    /*
     * [리팩토링-AS-IS] Mock 서비스에 대한 직접 의존성
     * @author 왕택준
     *
     * [주석 처리 이유]
     * 테스트용 Mock 서비스 구현체에 직접 의존하는 것은 확장성이 떨어지며, 실제 서비스로 교체 시
     * 이 클래스의 코드를 직접 수정해야 하는 단점이 있습니다. 이를 해결하기 위해,
     * GeminiApiService 인터페이스에 의존하도록 변경하여 DIP(의존성 역전 원칙)를 적용합니다.
     *
    private final MockAIServiceTj mockAiServiceTj;
    */

    /** [리팩토링-TO-BE] 실제 AI API 호출을 담당하는 서비스 인터페이스입니다. DI(의존성 주입)를 통해 실제 구현체(GeminiApiServiceImpl)가 주입됩니다. */
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

        String prompt = String.format(
            """
            당신은 웹소설 전문 작가입니다. 주어진 장르와 시놉시스를 바탕으로, 독자들의 흥미를 끌만한 소설 제목과 1화의 제목, 그리고 1화의 내용을 추천해주세요.
            반드시 아래의 JSON 형식에 맞춰서, JSON 코드 블록 없이 순수한 JSON 텍스트로만 답변해야 합니다.

            - 장르: "%s"
            - 시놉시스: "%s"

            {
              "novelTitle": "추천 소설 제목",
              "firstChapterTitle": "추천 1화 제목",
              "firstChapterContent": "추천 1화 내용 (200자 내외)"
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

        String prompt = String.format(
            """
            당신은 프로 웹소설 작가입니다. 주어진 소설의 전체 내용을 파악하고, 사용자의 요구사항을 반영하여 다음 이야기를 자연스럽게 이어주세요.
            반드시 아래의 JSON 형식에 맞춰서 답변해야 합니다.

            --- 전체 소설 내용 ---
            %s
            --------------------

            --- 사용자의 추가 요구사항 ---
            "%s"
            ------------------------

            {
              "suggestedTitle": "제안할 다음 이야기의 제목",
              "suggestedContent": "제안할 다음 이야기의 내용 (300자 내외)"
            }
            """,
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
     * AI가 반환한 전체 문자열에서 순수한 JSON 부분만 추출하는 헬퍼 메서드.
     * "```json\n{...}\n```" 와 같은 마크다운 형식의 응답에 대응합니다.
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
