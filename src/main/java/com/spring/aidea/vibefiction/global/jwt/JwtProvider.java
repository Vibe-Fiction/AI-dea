package com.spring.aidea.vibefiction.global.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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

    public String generateToken(String username, String role) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("role", role) // ROLE : USER, ADMIN
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer("Toy Project By ")
                .signWith(getSigningKey())
                .compact();
    }


    /**
     *  파싱된 JWT에서 권한(Role)을 추출하는 함수
     * @param token
     *
     */
    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }



    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 - Claims : 토큰의 내용
            getClaimsFromToken(token);
            return true;
        }catch (JwtException e) {
            log.warn("Invalid JWT token : {}",e.getMessage());
            return false;
        }
    }

    /**
     * 파싱된 JWT에서 사용자 이름을 추출하는 함수
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * JWT 토큰에서 실제 데이터를 추출
     * @param token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey()) // 여기서 예외 터지면 서버가 발급한 토큰이 아님
            .build()
            .parseSignedClaims(token)// 여기서 예외가 터지면 클라이언트 토큰이 위조됨
            .getPayload()
            ;
    }

    /**
     * JWT 토큰 발급에 필요한 서명 만들기
     * @return 서명 키 객체
     */
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
