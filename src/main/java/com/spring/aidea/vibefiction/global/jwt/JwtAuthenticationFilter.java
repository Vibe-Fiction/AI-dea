package com.spring.aidea.vibefiction.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 인증 필터
 * 클라이언트 모든 요청에 대해 토큰을 검사하는 자동화된 필터
 * @author 고동현
 */
@Component
@RequiredArgsConstructor
@Slf4j                                      // 요청
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 클라이언트의 요청에서 들어온 토큰을 뜯어와야함.
            String token = extractTokenFromHeader(request);

            // 서명, 토큰 위조 검사
            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
                // 토큰에서 사용자명을 추출
                String username = jwtProvider.getUsernameFromToken(token);
                // 토큰에서 ROLE 추출
                String role = jwtProvider.getRoleFromToken(token);

                List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // UserDetails 객체를 생성합니다.
                // 비밀번호가 필요 없으므로 "password"를 임시로 사용합니다.
                UserDetails userDetails = new User(username, "password", authorities);

                // 시큐리티에게 알려줄 인증정보(UserDetails 객체와 권한)를 생성
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities); // 권한 목록 전달

               /* // 시큐리티에게 알려줄 인증정보(사용자명, 권한) 생성
                UsernamePasswordAuthenticationToken auth
                    = new UsernamePasswordAuthenticationToken(
                        username, null, authorities); */


                // 스프링 시큐리티에게 인증 성공을 알려줌
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("JWT 인증 성공: {}", username);
            }
        }catch (Exception e) {
            log.error("JWT  인증 오류 발생 : {}",e.getMessage());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        // 1. 요청 헤더에서 Authorization 키를 파싱
        String bearerToken = request.getHeader("Authorization");

        // 2. 토큰을 파싱하면 앞에 접두사로 Bearer가 붙는데 이를 제거
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); // 7번 인덱스부터 끝까지
        }
        return null;
    }
}
