package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.SignUpRequestKO;
import com.spring.aidea.vibefiction.dto.response.UserResponseKO;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입 시 중복 검사와 비밀번호 암호화하여 회원가입하는 클래스입니다.
 * @author 고동현
 */
@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpServiceKO {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseKO signUp(SignUpRequestKO requestDto) {

        // 1. 중복 검사
        if (usersRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (usersRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (usersRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. DTO를 Users 엔티티로 변환
        Users newUser = Users.builder()
                .loginId(requestDto.getLoginId())
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .password(encodedPassword)
                .birthDate(requestDto.getBirthDate())
                .build();

        // 4. 리포지토리를 통해 DB에 저장
        Users saved = usersRepository.save(newUser);
        log.info("새로운 사용자 가입{}",saved);

        return UserResponseKO.from(saved);
    }
    }

