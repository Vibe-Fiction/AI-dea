package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalSummaryResponseTj;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.service.ProposalServiceTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 소설의 특정 회차에 대한 '이어쓰기 제안(Proposal)' 관련 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 독자들이 소설의 다음 이야기에 대해 자신의 아이디어를 제안하는 핵심 기능을 담당합니다.
 * 제안 생성은 JWT 인증을 필요로 하지만, 제안 목록 조회는 누구나 가능하도록 공개되어 있습니다.
 * 인증은 Spring Security의 JwtAuthenticationFilter를 통해 처리됩니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chapters/{chapterId}/proposals")
public class ProposalControllerTj {

    private final ProposalServiceTj proposalServiceTj;
    private final UsersRepository usersRepository;

    /**
     * 인증된 사용자가 특정 회차에 대한 새로운 이어쓰기 내용을 제안하고 등록합니다.
     *
     * 이 메서드는 JWT 토큰으로 사용자를 식별하며, 제안 내용은 해당 회차에 종속됩니다.
     * AI 추천을 통해 작성된 제안일 경우, 관련 AI 상호작용 로그 ID(aiLogId)를 포함할 수 있습니다.
     *
     * @param chapterId  새로운 제안을 등록할 대상 회차의 고유 ID.
     * @param req        생성할 제안의 제목(title), 내용(content), 그리고 선택적으로 AI 로그 ID(aiLogId)를 포함하는 DTO.
     * @return 성공 시 201 (Created) 상태 코드와 함께 생성된 제안 정보를 담은 {@link ApiResponse} 객체를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProposalCreateResponseTj>> create(
        @PathVariable Long chapterId,
        @Valid @RequestBody ProposalCreateRequestTj req) {

        // SecurityContextHolder를 통해 인증된 사용자의 ID를 조회
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Users proposer = usersRepository.findByLoginId(loginId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "인증된 사용자를 DB에서 찾을 수 없습니다."));
        Long proposerId = proposer.getUserId();

        // 인증된 제안자 ID와 요청 데이터를 서비스 레이어로 전달하여 제안 생성 처리
        ProposalCreateResponseTj result = proposalServiceTj.create(chapterId, proposerId, req);

        // 제안 리소스가 성공적으로 생성되었음을 알리는 201 Created 상태 코드로 응답
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("새로운 제안이 등록되었습니다.", result));
    }

    /**
     * 특정 회차에 등록된 모든 이어쓰기 제안의 요약 목록을 조회합니다.
     *
     * 이 엔드포인트는 공개 API로, 별도의 인증 절차 없이 누구나 호출할 수 있습니다.
     * 제안 목록은 독자들의 투표나 작가의 선택을 위해 화면에 노출되는 것을 목적으로 합니다.
     *
     * @param chapterId 제안 목록을 조회할 대상 회차의 고유 ID.
     * @return 성공 시 200 (OK) 상태 코드와 함께 해당 회차의 제안 요약 정보 목록을 담은 {@link ApiResponse}를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProposalSummaryResponseTj>>> list(@PathVariable Long chapterId) {
        // [비즈니스 로직] 해당 회차 ID에 종속된 모든 제안 목록을 서비스 레이어에서 조회
        List<ProposalSummaryResponseTj> result = proposalServiceTj.list(chapterId);

        // [비즈니스 로직] 조회된 제안 목록을 성공 응답(200 OK)에 담아 클라이언트에 반환
        return ResponseEntity.ok(ApiResponse.success("제안 목록 조회 성공", result));
    }
}
