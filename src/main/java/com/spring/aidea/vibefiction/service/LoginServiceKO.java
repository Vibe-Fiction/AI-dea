package com.spring.aidea.vibefiction.service;


import com.spring.aidea.vibefiction.dto.request.user.LoginRequestKO;
import com.spring.aidea.vibefiction.dto.response.user.AuthResponseKO;
import com.spring.aidea.vibefiction.dto.response.user.UserResponseKO;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.global.jwt.JwtProvider;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




/**
 * 로그인 시 아이디 or 이메일 검사 후 암호화된
 * 비밀번호를 평문화하여 비교 후 로그인하는 클래스입니다.
 *  @author 고동현
 */

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceKO {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthResponseKO authenticate(LoginRequestKO loginRequest) {

        String inputAccount = loginRequest.getLoginIdOrEmail();

        Users user = usersRepository.findByLoginId(inputAccount)
            .orElseGet(()-> usersRepository.findByEmail(inputAccount)
                .orElseThrow(
                    () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
                )
            );

        // 사용자 입력 비밀번호
        String inputPassword = loginRequest.getPassword();

        // DB에 저장된 비밀번호
        String StoredPassword = user.getPassword();

        if (!passwordEncoder.matches(inputPassword, StoredPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 로그인 성공 시 토큰 발급
        String token = jwtProvider.generateToken(user.getLoginId());
        log.info("사용자 로그인 {},", user.getLoginId());


        return AuthResponseKO.of(token, UserResponseKO.from(user));
    }

    public boolean checkDuplicateUsername(String loginId) {
        return usersRepository.existsByLoginId(loginId);
    }

    public boolean checkDuplicateEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

}
