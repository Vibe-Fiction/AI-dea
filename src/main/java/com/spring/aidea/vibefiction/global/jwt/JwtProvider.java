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
     * 사용자의 로그인 ID(username)와 고유 식별자(userId)를 모두 포함하는 새로운 버전의 JWT를 생성합니다. (메서드 오버로딩)
     * <p>
     * <b>[설계 의도]</b>
     * 기존 토큰(username만 포함)과 달리, 이 메서드는 JWT의 'payload'에 사용자의 PK인 {@code userId}를
     * 'claim'으로 추가합니다. 이를 통해, API 요청이 들어왔을 때 매번 데이터베이스를 조회하여
     * 사용자를 찾는 과정을 생략하고, 토큰 자체만으로 사용자를 식별할 수 있게 됩니다.
     * 이는 시스템의 성능을 향상시키고 DB 부하를 줄이는 데 매우 효과적입니다.
     *
     * @param username 사용자의 로그인 ID. JWT의 'sub'(subject) 클레임으로 설정됩니다.
     * @param userId   사용자의 고유 식별자(PK). JWT의 커스텀 클레임('userId')으로 추가됩니다.
     * @return 생성된 JWT 문자열.
     * @author 왕택준
     * @since 2025.08
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
            // [Standard Claim] 'sub' (Subject): 토큰의 주체(소유자)를 나타냅니다. 여기서는 로그인 ID를 사용합니다.
            .subject(username)
            // [Custom Claim] 'userId': 우리 서비스에서만 사용하는 비표준 클레임. 사용자의 PK를 담아 DB 조회를 최소화하는 것이 목적입니다.
            .claim("userId", userId)
            // [Standard Claim] 'iat' (Issued At): 토큰이 발급된 시간을 나타냅니다.
            .issuedAt(now)
            // [Standard Claim] 'exp' (Expiration Time): 토큰의 만료 시간을 나타냅니다.
            .expiration(expiryDate)
            // [Standard Claim] 'iss' (Issuer): 토큰 발급자를 나타냅니다.
            .issuer("Toy Project By TJ") // [TJ] 발급자 명확화
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
