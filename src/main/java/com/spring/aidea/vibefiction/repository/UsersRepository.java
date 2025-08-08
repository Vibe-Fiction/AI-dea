package com.spring.aidea.vibefiction.repository;

import com.spring.aidea.vibefiction.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<엔티티 클래스, 엔티티 ID 타입>을 상속받아 Repository를 정의
public interface UsersRepository extends JpaRepository<Users, Long> {

}