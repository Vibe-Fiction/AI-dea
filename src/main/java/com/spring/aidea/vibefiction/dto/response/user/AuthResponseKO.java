package com.spring.aidea.vibefiction.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 완료 후 클라이언트에게 전송할 내용
 * @author 고동현
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseKO {

    private String token; // JWT 토큰
    @Builder.Default
    private String tokenType = "Bearer"; // Bearer 고정
    private UserResponseKO user; // 로그인한 유저의 정보

    // 정적 팩토리 메서드
    public static AuthResponseKO of(String token, UserResponseKO user) {
        return AuthResponseKO.builder()
            .token(token)
            .user(user)
            .build();
    }


}
