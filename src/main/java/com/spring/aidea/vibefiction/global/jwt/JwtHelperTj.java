package com.spring.aidea.vibefiction.global.jwt;

import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * [TJ] JWT 토큰에서 사용자 정보를 안전하게 추출하는 헬퍼 클래스입니다.
 * 컨트롤러의 중복 코드를 줄이고, 토큰 파싱 관련 로직을 중앙에서 관리합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtHelperTj {

    private final JwtProperties jwtProperties;
    private final UsersRepository usersRepository; // [ai] 하위 호환성을 위해 DB 조회가 필요합니다.

    /**
     * [TJ] 컨트롤러에서 사용할 가장 중요한 메인 메서드입니다.
     * Authorization 헤더 값("Bearer ...")을 받아 사용자 ID(PK)를 추출합니다.
     *
     * @param authHeader "Bearer <token>" 형식의 전체 헤더 값
     * @return 추출된 사용자 ID. 토큰이 유효하지 않거나 사용자를 찾을 수 없으면 null을 반환합니다.
     */
    public Long extractUserId(String authHeader) {
        Claims claims = getClaimsFromToken(authHeader);

        // [ai] 토큰 자체가 유효하지 않으면 즉시 null 반환
        if (claims == null) {
            return null;
        }

        // [ai] Case 1: (가장 좋은 경우) 'userId' 클레임이 숫자로 존재할 때 -> DB 조회 없이 즉시 반환
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        }

        // [ai] Case 2: (하위 호환) 'userId' 클레임이 없으면, subject(loginId)를 이용해 DB에서 조회
        String loginId = claims.getSubject();
        if (loginId != null) {
            return usersRepository.findByLoginId(loginId)
                .map(Users::getUserId) // 찾은 User 엔티티에서 userId만 꺼내기
                .orElse(null);      // DB에 해당 loginId가 없으면 null 반환
        }

        // [ai] 모든 케이스에 해당하지 않으면 null 반환
        return null;
    }

    /**
     * 토큰 문자열을 파싱하여 클레임(정보 조각)들을 추출합니다.
     *
     * @param authHeader "Bearer <token>" 형식의 헤더 값
     * @return 클레임 객체. 파싱 실패 시 null을 반환합니다.
     */
    private Claims getClaimsFromToken(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return null;
        }

        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            // [ai] 토큰이 만료되었거나, 서명이 잘못되었거나, 형식이 틀리는 등 모든 예외 상황을 처리합니다.
            // 유효하지 않은 토큰은 null로 처리하여 안전하게 만듭니다.
            return null;
        }
    }

    /**
     * "Bearer " 접두사를 제거하고 순수한 토큰 문자열만 추출합니다.
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * application.yml의 secret 값으로 서명 키를 생성합니다.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
