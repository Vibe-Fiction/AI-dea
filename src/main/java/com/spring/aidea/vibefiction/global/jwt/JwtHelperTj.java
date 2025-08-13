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
 * JWT(Json Web Token)에서 사용자 정보를 안전하게 추출하는 로직을 중앙에서 관리하는 헬퍼 클래스입니다.
 *
 * 이 클래스는 컨트롤러 등 여러 곳에 흩어져 있던 토큰 파싱 및 사용자 식별 로직을 한 곳으로 모아
 * 코드의 중복을 제거하고 유지보수성을 향상시키는 것을 목적으로 합니다.
 * <p>
 * <b>[주요 설계]</b>
 * 새로운 방식(userId 클레임 직접 사용)과 기존 방식(loginId로 DB 조회)의 토큰을 모두 지원하여
 * 시스템의 하위 호환성을 보장하도록 설계되었습니다.
 *
 * @author 왕택준
 * @since 2025.08
 */
@Component
@RequiredArgsConstructor
public class JwtHelperTj {

    /** JWT 생성 및 검증에 필요한 설정값(secret key, 만료 시간 등)을 담고 있는 프로퍼티 클래스입니다. */
    private final JwtProperties jwtProperties;
    /** 'userId' 클레임이 없는 구형 토큰과의 하위 호환성을 위해, 'loginId'로 사용자를 조회할 때 사용됩니다. */
    private final UsersRepository usersRepository;

    /**
     * HTTP 'Authorization' 헤더 값에서 JWT를 추출하고 파싱하여, 최종적으로 사용자의 고유 ID(PK)를 반환합니다.
     * <p>
     * <b>[처리 로직 및 하위 호환성]</b>
     * 이 메서드는 시스템의 성능과 하위 호환성을 모두 고려하여 다음과 같은 순서로 동작합니다.
     * <ol>
     *  <li><b>최적 경로:</b> 토큰의 커스텀 클레임에 'userId'가 포함되어 있는지 확인합니다.
     *      존재할 경우, 데이터베이스 조회 없이 즉시 해당 ID를 반환하여 최상의 성능을 보장합니다.</li>
     *  <li><b>하위 호환 경로:</b> 'userId' 클레임이 없는 구형 토큰의 경우, 'subject' 클레임에서 'loginId'를
     *      추출하여 데이터베이스를 조회하고, 해당 사용자의 ID를 찾아 반환합니다.</li>
     * </ol>
     *
     * @param authHeader "Bearer {token}" 형식의 전체 Authorization 헤더 값.
     * @return 추출된 사용자의 고유 ID({@code Long}). 토큰이 유효하지 않거나 사용자를 식별할 수 없는 모든 경우 {@code null}을 반환합니다.
     */
    public Long extractUserId(String authHeader) {
        // [1. 토큰 파싱] 헤더에서 토큰을 추출하고 유효성을 검증하여 클레임(정보)을 가져옵니다.
        Claims claims = getClaimsFromToken(authHeader);

        // 토큰 자체가 유효하지 않으면(null), 이후 로직을 수행하지 않고 즉시 종료합니다.
        if (claims == null) {
            return null;
        }

        // [2. 분기 처리: userId 클레임 확인]
        // [분기 1: 최적 경로] 'userId' 클레임이 숫자로 존재할 때 -> DB 조회 없이 즉시 반환
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        }

        // [분기 2: 하위 호환 경로] 'userId' 클레임이 없으면, subject(loginId)를 이용해 DB에서 조회
        String loginId = claims.getSubject();
        if (loginId != null) {
            return usersRepository.findByLoginId(loginId)
                    .map(Users::getUserId) // 찾은 User 엔티티에서 userId만 꺼내기
                    .orElse(null);      // DB에 해당 loginId를 가진 사용자가 없으면 null 반환
        }

        // 모든 케이스에 해당하지 않으면(비정상 토큰), 안전하게 null 반환
        return null;
    }

    /**
     * Authorization 헤더 문자열에서 순수 토큰을 추출하고, 서명 검증을 거쳐 내부 정보(Claims)를 반환하는 private 헬퍼 메서드입니다.
     *
     * @param authHeader "Bearer {token}" 형식의 헤더 값.
     * @return 파싱된 클레임 객체. 토큰이 유효하지 않은 모든 경우(만료, 서명 불일치, 형식 오류 등)에는 {@code null}을 반환합니다.
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
            // [보안 설계] 만료(Expired), 서명 오류(Signature), 형식 오류(Malformed) 등 토큰 관련 모든 예외를
            // 포괄적으로 처리하여, 유효하지 않은 토큰에 대해서는 항상 null을 반환함으로써 상위 로직의 안정성을 보장합니다.
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
     * application.yml에 설정된 secret 값으로 서명 검증에 사용할 {@link SecretKey}를 생성합니다.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
