package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.response.novel.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.dto.response.user.MyPageResponseSH;
import com.spring.aidea.vibefiction.dto.response.user.UserResponseKO;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.repository.custom.NovelsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageServiceSH {

    public final NovelsRepository novelsRepository;
    public final UsersRepository usersRepository;


    /**
     *
     * MYPAGE 랜더링에 필요한 사용자의 정보와 사용자가 참여한 소설리스트를 반환하는 매서드
     *
     * @param userId - 사용자 Pk id
     * @return 사용자의 기본 정보와 원작자로 참여한 소설리스트를 반환합니다.
     */
    public MyPageResponseSH findUserAndNovelsById(Long userId) {

        Users users = usersRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Novels> novels = novelsRepository.findNovelsByAuthorId(userId);

        return MyPageResponseSH.from(users, novels);
    }




}
