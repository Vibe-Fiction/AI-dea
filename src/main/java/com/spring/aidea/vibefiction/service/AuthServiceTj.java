/*
package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.auth.LoginRequestTj;
import com.spring.aidea.vibefiction.dto.response.auth.LoginResponseTj;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.jwt.JwtProvider;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

*/
/**
 * 사용자 인증(로그인)과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * 이 서비스는 클라이언트로부터 받은 로그인 정보를 바탕으로 사용자를 검증하고,
 * 인증에 성공하면 시스템의 보호된 리소스에 접근할 수 있는 권한 증표인 JWT를 발급합니다.
 * <p>
 * <b>[보안 설계]</b>
 * 비밀번호 비교 시, 절대 평문을 비교하지 않습니다. Spring Security의 {@link PasswordEncoder}를 사용하여
 * 사용자가 입력한 평문 비밀번호를 해시화한 값과 데이터베이스에 저장된 해시 값을 안전하게 비교합니다.
 *
 * @author 왕택준
 * @since 2025.08
 *//*

@Service
@RequiredArgsConstructor
public class AuthServiceTj {

    */
/** 사용자 정보를 데이터베이스에서 조회하기 위한 저장소입니다. *//*

    private final UsersRepository usersRepository;

    */
/** 단방향 해시 함수(예: bcrypt)를 사용하여 비밀번호를 안전하게 암호화하고 검증하는 컴포넌트입니다. *//*

    private final PasswordEncoder passwordEncoder;

    */
/** 인증 성공 후, 사용자의 정보를 담은 JWT를 생성하고 관리하는 컴포넌트입니다. *//*

    private final JwtProvider jwtProvider;

    */
/**
     * 사용자의 아이디와 비밀번호를 검증하여 로그인을 처리하고, 성공 시 JWT를 발급합니다.
     * <p>
     * <b>[인증 절차]</b>
     * <ol>
     *  <li>요청된 아이디({@code loginId})로 사용자를 조회합니다.</li>
     *  <li>사용자가 존재하면, 요청된 비밀번호와 DB에 저장된 해시화된 비밀번호를 비교합니다.</li>
     *  <li>검증에 모두 성공하면, 사용자의 정보를 담은 새로운 JWT를 생성하여 반환합니다.</li>
     * </ol>
     *
     * @param req 사용자의 로그인 아이디와 평문 비밀번호를 담은 {@link LoginRequestTj}.
     * @return 인증 성공 시 JWT 액세스 토큰을 포함한 {@link LoginResponseTj}.
     * @throws IllegalArgumentException 아이디가 존재하지 않거나 비밀번호가 일치하지 않을 경우 발생합니다.
     *                                  (참고: 실제 서비스에서는 보안을 위해 "아이디 또는 비밀번호가 잘못되었습니다." 와 같이
     *                                  모호한 메시지를 사용하는 것이 권장됩니다.)
     *//*

    public LoginResponseTj login(LoginRequestTj req) {
        // [1. 사용자 조회] 요청된 loginId를 기반으로 데이터베이스에서 사용자 정보를 찾습니다.
        // 사용자가 없으면, orElseThrow를 통해 즉시 예외를 발생시켜 이후 로직을 중단합니다.
        Users user = usersRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // [2. 비밀번호 검증] passwordEncoder.matches()를 통해 평문 비밀번호와 해시된 비밀번호를 안전하게 비교합니다.
        // 이 메서드는 내부적으로 salt 값을 고려하여 비교하므로, 동일한 평문이라도 매번 다른 해시값과 비교할 수 있습니다.
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // [3. JWT 발급] 모든 검증을 통과하면, 사용자의 loginId와 userId를 payload에 담아 토큰을 생성합니다.
        String token = jwtProvider.generateToken(user.getLoginId(), user.getUserId());

        // [4. 성공 응답 반환] 생성된 토큰을 DTO에 담아 클라이언트에게 전달합니다.
        return new LoginResponseTj(token, "로그인에 성공했습니다.");
    }
}
*/
