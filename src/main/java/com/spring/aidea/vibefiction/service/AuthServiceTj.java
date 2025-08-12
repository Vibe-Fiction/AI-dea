package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.auth.LoginRequestTj;
import com.spring.aidea.vibefiction.dto.response.auth.LoginResponseTj;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.jwt.JwtProvider;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Spring Security 의존성 필요
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceTj {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponseTj login(LoginRequestTj req) {
        // 1. 사용자 조회
        Users user = usersRepository.findByLoginId(req.getLoginId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. (핵심) JwtProvider에 loginId와 "조회한 userId"를 함께 전달하여 토큰 생성
        String token = jwtProvider.generateToken(user.getLoginId(), user.getUserId());

        return new LoginResponseTj(token, "로그인에 성공했습니다.");
    }
}
