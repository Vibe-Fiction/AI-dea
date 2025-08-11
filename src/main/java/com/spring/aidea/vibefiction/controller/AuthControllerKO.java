package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.SignUpRequestKO;
import com.spring.aidea.vibefiction.dto.response.UserResponseKO;
import com.spring.aidea.vibefiction.service.SignUpServiceKO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthControllerKO {

    private final SignUpServiceKO signUpServiceKO;


    /**
     *  회원가입 API
     *  POST : /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequestKO requestDto) {
        log.info("회원가입 요청 {}",requestDto.getLoginId());

        UserResponseKO response = signUpServiceKO.signUp(requestDto);

        return ResponseEntity.ok().body(response);

    }
}
