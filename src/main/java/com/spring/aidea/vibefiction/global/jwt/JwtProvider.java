package com.spring.aidea.vibefiction.global.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성,검증,파싱 기능 유틸 클래스입니다.
 * @author 고동현
 */


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {


    private final JwtProperties jwtProperties;

    /**
     * JWT 토큰 생성 메서드
     * @param username - 발급 대상의 사용자 이름
     * @return - JWT 토큰 문자열 (암호화)
     */

    public String generateToken(String username) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer("Toy Project By ")
                .signWith(getSigningKey())
                .compact();
    }


    /**
     * JWT 토큰 발급에 필요한 서명 만들기
     * @return 서명 키 객체
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
