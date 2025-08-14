package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.user.LoginRequestKO;
import com.spring.aidea.vibefiction.dto.request.user.SignUpRequestKO;
import com.spring.aidea.vibefiction.dto.response.user.AuthResponseKO;
import com.spring.aidea.vibefiction.dto.response.user.UserResponseKO;
import com.spring.aidea.vibefiction.global.common.ApiResponse;
import com.spring.aidea.vibefiction.service.LoginServiceKO;
import com.spring.aidea.vibefiction.service.SignUpServiceKO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthControllerKO {

    private final SignUpServiceKO signUpServiceKO;
    private final LoginServiceKO loginServiceKO;


    /**
     *  회원가입 API
     *  POST : /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequestKO requestDto) {
        log.info("회원가입 요청 {}",requestDto.getLoginId());

        UserResponseKO response = signUpServiceKO.signUp(requestDto);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다.",response));

    }

    /**
     *  로그인 API
     *  POST: /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestKO requestDto) {
        log.info("로그인요청 {}",requestDto.getLoginIdOrEmail());

        AuthResponseKO response = loginServiceKO.authenticate(requestDto);


        return ResponseEntity.ok().body(
                ApiResponse.success("로그인이 완료되었습니다.",response)
        );
    }

    /**
     * 사용자명 중복 체크 API
     * GET /api/auth/check-username?username=xxx
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String loginId) {

        boolean exists = loginServiceKO.checkDuplicateUsername(loginId);

        return ResponseEntity.ok()
            .body(ApiResponse.success(
                exists ? "이미 사용 중인 사용자명입니다." : "사용 가능한 사용자명입니다."
                , exists
            ));
    }

    /**
     * 이메일 중복 체크 API
     * GET /api/auth/check-email?email=xxx
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {

        boolean exists = loginServiceKO.checkDuplicateEmail(email);

        return ResponseEntity.ok()
            .body(ApiResponse.success(
                exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다."
                , exists
            ));
    }

}
