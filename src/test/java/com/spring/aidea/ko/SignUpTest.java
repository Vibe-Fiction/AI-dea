package com.spring.aidea.ko;

import com.spring.aidea.vibefiction.dto.request.user.SignUpRequestKO;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.service.SignUpServiceKO;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 회원가입을 위한 테스트 클래스입니다.
 * @author 고동현
 */

@Slf4j
@SpringBootTest
@Transactional
public class SignUpTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    SignUpServiceKO signUpServiceKO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("정상적인 정보로 회원가입 시 성공해야한다.")
    void SignUpSuccessTest() {
        //given
        SignUpRequestKO requestDto = SignUpRequestKO.builder()
                .loginId("test1234")
                .email("test@test.com")
                .password("Test1234@")
                .nickname("테스트용")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        // when
        signUpServiceKO.signUp(requestDto);

        em.flush();
        em.clear();

        // then
        Users savedUser = usersRepository.findByLoginId(requestDto.getLoginId()).orElseThrow(
                () -> new IllegalArgumentException("테스트 실패: 저장된 사용자를 찾을 수 없습니다."));

        // 1. 저장된 비밀번호가 원본과 다른지 확인 (암호화되었는지)
        assertThat(savedUser.getPassword()).isNotEqualTo(requestDto.getPassword());

        // 2. 원본 비밀번호와 암호화된 비밀번호를 비교했을 때 일치하는지 확인
        assertThat(passwordEncoder.matches(requestDto.getPassword(), savedUser.getPassword())).isTrue();

        System.out.println("savedUser = " + savedUser);
    }


}
