package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<엔티티 클래스, 엔티티 ID 타입>을 상속받아 Repository를 정의
public interface UsersRepository extends JpaRepository<Users, Long> {


    /**
     * 사용자 조회 및 로그인 시 중복 확인 메서드
     * @author 고동현
     */
    Optional<Users> findByLoginId(String loginId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByNickname(String nickname);

    /**
     *
     * @param userId - user PK
     * @return - UserId로 찾은 사용자를 반환
     */
    Optional<Users> findByUserId(Long userId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);


}
