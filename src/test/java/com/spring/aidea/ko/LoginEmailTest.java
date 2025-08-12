package com.spring.aidea.ko;

import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class LoginEmailTest {


    @Autowired
    UsersRepository userRepository;

    @BeforeEach
    void setUp() {
        Users user = Users.builder()
                .loginId("testUser")
                .email("test@example.com")
                .password("Password123@")
                .nickname("테스트")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        userRepository.save(user);

    }
    @Test
    @DisplayName("사용자 이메일로 조회 테스트")
    void findByUserEmailTest() {
        //given
        String email = "test@example.com";
        //when
        Users foundUser = userRepository.findByEmail(email).orElseThrow();
        //then
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getLoginId());
    }




}
