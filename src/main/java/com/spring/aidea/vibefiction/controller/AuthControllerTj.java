package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.auth.LoginRequestTj;
import com.spring.aidea.vibefiction.dto.response.auth.LoginResponseTj;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.service.AuthServiceTj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 인증 관련 API 요청을 처리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 사용자 로그인과 같은 인증 프로세스를 담당하며, 성공적인 인증 후
 * 클라이언트가 서비스의 다른 보호된 리소스에 접근할 수 있도록 JWT를 발급하는 역할을 합니다.
 * 여기에 포함된 엔드포인트는 인증 없이 공개적으로 접근 가능합니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllerTj {

    private final AuthServiceTj authServiceTj;

    /**
     * 사용자의 아이디와 비밀번호를 받아 로그인을 처리하고, 인증 토큰을 발급합니다.
     *
     * 이 메서드는 클라이언트로부터 받은 로그인 정보를 {@link AuthServiceTj}로 전달하여 인증을 위임합니다.
     * 인증에 성공하면, 서비스 접근에 필요한 JWT(Access Token)와 리프레시 토큰(Refresh Token)을
     * 포함한 사용자 정보를 반환합니다.
     *
     * @param req 사용자의 로그인 아이디(loginId)와 비밀번호(password)를 포함하는 요청 DTO.
     * @return 성공 시 200 (OK) 상태 코드와 함께 토큰 및 사용자 정보가 담긴 {@link ApiResponse} 객체를 반환합니다.
     *         (참고: 인증 실패(예: 비밀번호 불일치)의 경우, 서비스 레이어의 예외 처리 핸들러에 의해
     *         401 (Unauthorized)과 같은 적절한 HTTP 상태 코드가 반환될 수 있습니다.)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseTj>> login(@RequestBody LoginRequestTj req) {
        // [TJ] 로그인 요청 데이터를 서비스 레이어로 전달하여 인증 및 토큰 발급 처리
        LoginResponseTj responseData = authServiceTj.login(req);

        // [비즈니스 로직] 인증 성공 시, 성공 응답(200 OK)과 함께 발급된 토큰 정보를 클라이언트에 반환
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", responseData));
    }
}
