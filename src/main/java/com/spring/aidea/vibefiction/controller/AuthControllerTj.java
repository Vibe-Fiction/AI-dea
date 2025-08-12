package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.auth.LoginRequestTj;
import com.spring.aidea.vibefiction.dto.response.auth.LoginResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.service.AuthServiceTj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러.
 * - 로그인 처리
 * - JWT 토큰 발급
 *
 * 이 컨트롤러의 API는 인증 없이 호출 가능.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/auth")
public class AuthControllerTj {

    private final AuthServiceTj authServiceTj;

    /**
     * 사용자 로그인 API.
     *
     * @param req 로그인 요청 DTO (아이디, 비밀번호)
     * @return    JWT 액세스 토큰 및 사용자 정보
     * @status 200 로그인 성공
     * @status 401 로그인 실패 (아이디/비밀번호 불일치)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseTj>> login(@RequestBody LoginRequestTj req) {
        LoginResponseTj responseData = authServiceTj.login(req);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", responseData));
    }
}
