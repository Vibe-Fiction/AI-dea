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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 기반의 보조 기능(소설 추천, 이어쓰기 제안)을 처리하는 핵심 서비스 클래스입니다.
 *
 * 이 서비스는 AI 모델과의 상호작용을 담당하며, 프롬프트 생성, AI 응답 파싱,
 * 그리고 모든 상호작용에 대한 로그를 데이터베이스에 기록하는 책임을 가집니다.
 * <p>
 * <b>[설계]</b> 현재는 외부 AI API 호출 없이 Mock 데이터를 반환하는 {@link MockAIServiceTj}를 사용하며,
 * 향후 실제 AI 연동 시 이 부분만 의존성 주입(DI)을 통해 교체하면 되도록 유연하게 설계되었습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class AiAssistServiceTj {

    /** 실제 AI API 호출을 대체하는 Mock 서비스입니다. DI(의존성 주입)를 통해 실제 서비스로 쉽게 교체 가능합니다. */
    private final MockAIServiceTj mockAiServiceTj;
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
     * @throws RuntimeException         AI 응답(JSON) 파싱에 실패한 경우.
     */
    @Transactional
    public AiRecommendNovelResponseTj recommendForNewNovel(Long userId, AiRecommendNovelRequestTj req) {
        // [1. 사용자 조회] 요청의 주체가 되는 사용자 엔티티를 조회합니다.
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // [2. 프롬프트 생성] AI 모델에 명확한 역할을 부여하고 원하는 출력 형식을 지정하는 프롬프트 엔지니어링 과정입니다.
        String prompt = String.format(
                // ... (프롬프트 내용은 동일)
                """
                당신은 웹소설 전문 작가입니다. 주어진 장르와 시놉시스를 바탕으로, 독자들의 흥미를 끌만한 소설 제목과 1화의 제목, 그리고 1화의 내용을 추천해주세요.
                반드시 아래의 JSON 형식에 맞춰서 답변해야 합니다.

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

        // [3. AI 서비스 호출] 프롬프트를 전달하여 AI로부터 추천 결과를 받습니다. (현재 Mock 서비스 사용)
        String aiResultJson = mockAiServiceTj.getNovelRecommendation(prompt);

        // [4. 핵심 비즈니스 로직] 사용자의 요청과 AI의 응답을 모두 로그로 기록하여, 추후 서비스 분석 및 문제 추적에 활용합니다.
        AiInteractionLogs log = AiInteractionLogs.builder()
                .user(user)
                .type(AiInteractionLogs.AiInteractionType.NOVEL_CREATION)
                .prompt(prompt)
                .result(aiResultJson)
                .build();
        aiInteractionLogsRepository.save(log);

        // [5. 결과 파싱 및 반환] AI 응답(JSON)을 파싱하여 클라이언트에게 전달할 DTO 객체로 변환합니다.
        try {
            JsonNode rootNode = objectMapper.readTree(aiResultJson);
            // .path()는 키가 없어도 예외를 던지지 않고 MissingNode를 반환하므로, .asText()의 기본값으로 안전하게 처리 가능
            return AiRecommendNovelResponseTj.builder()
                    .logId(log.getLogId())
                    .novelTitle(rootNode.path("novelTitle").asText("제목 추천 실패"))
                    .firstChapterTitle(rootNode.path("firstChapterTitle").asText("1화 제목 추천 실패"))
                    .firstChapterContent(rootNode.path("firstChapterContent").asText("1화 내용 추천 실패"))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답(JSON) 파싱에 실패했습니다.", e);
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
     * @throws RuntimeException         AI 응답(JSON) 파싱에 실패한 경우.
     */
    @Transactional
    public AiContinueResponseTj continueForChapter(Long userId, Long chapterId, AiContinueRequestTj req) {
        // [1. 엔티티 조회] 요청 주체인 사용자와 이어쓰기의 기준이 될 회차 엔티티를 조회합니다.
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        Chapters baseChapter = chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("기반 회차를 찾을 수 없습니다. ID: " + chapterId));

        // [2. 컨텍스트 생성] AI가 이야기의 일관성을 유지할 수 있도록, 이전 스토리를 모두 조회하여 하나의 컨텍스트로 만듭니다.
        String fullStoryContext = buildFullStoryContext(baseChapter);

        // [3. 프롬프트 생성] 전체 내용과 사용자 요구사항을 결합하여 AI에게 전달할 최종 프롬프트를 구성합니다.
        String prompt = String.format(
                // ... (프롬프트 내용은 동일)
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

        // [4. AI 서비스 호출]
        String aiResultJson = mockAiServiceTj.getContinueProposal(prompt);

        // [5. 핵심 비즈니스 로직] AI와의 상호작용을 로그로 기록합니다.
        AiInteractionLogs log = AiInteractionLogs.builder()
                .user(user)
                .type(AiInteractionLogs.AiInteractionType.PROPOSAL_GENERATION)
                .prompt(prompt)
                .result(aiResultJson)
                .basedOnChapter(baseChapter)
                .build();
        aiInteractionLogsRepository.save(log);

        // [6. 결과 파싱 및 반환]
        try {
            JsonNode rootNode = objectMapper.readTree(aiResultJson);
            return AiContinueResponseTj.builder()
                    .logId(log.getLogId())
                    .suggestedTitle(rootNode.path("suggestedTitle").asText("제목 제안 실패"))
                    .suggestedContent(rootNode.path("suggestedContent").asText("내용 제안 실패"))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답(JSON) 파싱에 실패했습니다.", e);
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
                // [데이터 정합성] 회차 순서가 보장되어야 하므로 chapterNumber 기준으로 명시적 정렬
                .sorted((c1, c2) -> c1.getChapterNumber().compareTo(c2.getChapterNumber()))
                .map(c -> String.format("제%d화: %s\n%s", c.getChapterNumber(), c.getTitle(), c.getContent()))
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
