package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.chapter.ChapterCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.chapter.ChapterCreateResponseTj;
import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Proposals;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.ProposalsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소설의 새로운 회차를 생성하고 관리하는 비즈니스 로직을 담당하는 서비스 클래스입니다.
 *
 * 이 서비스는 회차 생성 요청이 들어왔을 때, 관련 도메인 엔티티(소설, 작가, 제안)들의 정합성을 검증하고,
 * 비즈니스 규칙(회차 번호 자동 증가 등)에 따라 새로운 회차를 생성하여 데이터베이스에 저장하는 책임을 가집니다.
 * 특히, 사용자의 '이어쓰기 제안(Proposal)'을 채택하여 정식 회차로 승격시키는 중요한 로직을 포함합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Service
@RequiredArgsConstructor
public class ChapterServiceTj {

    /** 새로운 회차를 저장하고, 마지막 회차 번호를 조회하는 데 사용됩니다. */
    private final ChaptersRepository chaptersRepository;
    /** 새로운 회차를 연결할 부모 소설 엔티티를 조회하기 위해 사용됩니다. */
    private final NovelsRepository novelsRepository;
    /** 회차의 작성자(작가) 엔티티를 조회하기 위해 사용됩니다. */
    private final UsersRepository usersRepository;
    /** 회차의 원본이 되는 '이어쓰기 제안' 엔티티를 조회하는 데 사용됩니다. */
    private final ProposalsRepository proposalsRepository;

    /**
     * 특정 소설에 새로운 회차를 생성하고 데이터베이스에 저장합니다.
     *
     * 이 메서드는 회차 생성에 필요한 모든 엔티티(소설, 작가)를 조회하고, 다음 회차 번호를 계산하는 등
     * 핵심적인 생성 과정을 담당합니다. 또한, 독자의 '이어쓰기 제안'을 기반으로 회차를 생성하는 시나리오도 지원합니다.
     * 모든 과정은 하나의 트랜잭션으로 묶여 원자성을 보장합니다.
     *
     * @param novelId        새로운 회차를 추가할 {@link Novels}의 고유 ID.
     * @param authorId       회차를 작성한 사용자({@link Users})의 고유 ID.
     * @param req            생성할 회차의 제목과 본문을 담은 {@link ChapterCreateRequestTj}.
     * @param fromProposalId (선택 사항) 이 회차의 원본이 되는 '이어쓰기 제안'의 고유 ID. 제안 없이 직접 작성하는 경우 {@code null}이 될 수 있습니다.
     * @return 생성된 회차의 고유 ID와 회차 번호를 담은 {@link ChapterCreateResponseTj}.
     * @throws IllegalArgumentException 요청된 ID에 해당하는 소설, 작가, 또는 제안 엔티티가 존재하지 않을 경우.
     */
    @Transactional
    public ChapterCreateResponseTj create(Long novelId, Long authorId, ChapterCreateRequestTj req, Long fromProposalId) {
        // [1. 부모 엔티티 조회] 회차를 귀속시킬 부모 소설 엔티티를 조회합니다.
        Novels novel = novelsRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("회차를 추가할 소설을 찾을 수 없습니다. ID: " + novelId));

        // [2. 작성자 조회] 회차의 소유권을 명시할 작성자(작가) 엔티티를 조회합니다.
        Users author = usersRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다. ID: " + authorId));

        // [3. 비즈니스 규칙: 회차 번호 계산] 마지막 회차를 조회하여 그 번호에 1을 더하거나, 첫 회차일 경우 1로 초기화합니다.
        // 이 로직은 동시성 문제가 발생할 수 있으나, 현재 서비스 단계에서는 허용 가능한 수준으로 판단합니다. (TODO: 비관적/낙관적 락 고려)
        Integer nextChapterNumber = chaptersRepository.findTopByNovel_NovelIdOrderByChapterNumberDesc(novelId)
                .map(lastChapter -> lastChapter.getChapterNumber() + 1)
                .orElse(1);

        // [4. 선택적 로직: 원본 제안 조회] 제안 ID가 주어진 경우에만 해당 제안을 조회하여 연결 준비를 합니다.
        Proposals fromProposal = null;
        if (fromProposalId != null) {
            fromProposal = proposalsRepository.findById(fromProposalId)
                    .orElseThrow(() -> new IllegalArgumentException("채택할 제안을 찾을 수 없습니다. ID: " + fromProposalId));
        }

        // [5. 엔티티 생성] 조회된 정보들을 바탕으로 새로운 회차 엔티티를 생성합니다.
        Chapters newChapter = Chapters.builder()
                .novel(novel)
                .author(author)
                .chapterNumber(nextChapterNumber)
                .title(req.getTitle())
                .content(req.getContent())
                .fromProposal(fromProposal)
                .build();

        // [6. 영속화] 생성된 회차 엔티티를 데이터베이스에 저장합니다. (@Transactional에 의해 커밋 시점에 DB에 반영)
        chaptersRepository.save(newChapter);

        // [7. 결과 반환] 클라이언트에게 생성된 리소스의 정보를 전달하기 위해 응답 DTO를 구성합니다.
        return ChapterCreateResponseTj.builder()
                .chapterId(newChapter.getChapterId())
                .chapterNumber(newChapter.getChapterNumber())
                .build();
    }
}
