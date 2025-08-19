package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.user.UserUpdateRequestSH;
import com.spring.aidea.vibefiction.dto.response.user.MyPageResponseSH;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import com.spring.aidea.vibefiction.service.MyPageServiceSH;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/my-page")
@Slf4j
@RequiredArgsConstructor
public class MyPageControllerSH {

    public final MyPageServiceSH myPageServiceSH;
    public final UsersRepository usersRepository;

    @GetMapping
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal String loginId){

        Users users = usersRepository.findByLoginId(loginId)
            .orElseGet(() -> usersRepository.findByEmail(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
            ));

        Long userid = users.getUserId();


        MyPageResponseSH userAndNovels = myPageServiceSH.findUserAndNovelsById(userid);


        return ResponseEntity.ok(userAndNovels);
    }



    /**
     * 사용자 프로필 업데이트
     * multipart/form-data로 전송된 데이터를 처리합니다.
     *
     * @param loginId 토큰에 저장된 사용자 ID
     * @param nickname 새 닉네임 (선택사항)
     * @param email 새 이메일 (선택사항)
     * @param password 새 비밀번호 (선택사항)
     * @param profileImage 새 프로필 이미지 (선택사항)
     * @return 업데이트 결과
     */
    @PostMapping
    public ResponseEntity<?> updateProfile(
        @AuthenticationPrincipal String loginId,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) String currentPassword,  // 이 줄 추가
        @RequestParam(required = false) MultipartFile profileImage
    ) {


        UserUpdateRequestSH updateRequest = new UserUpdateRequestSH();

        // null이 아닌 값만 설정
        if (nickname != null && !nickname.trim().isEmpty()) {
            updateRequest.setNickname(nickname);
        }
        if (email != null && !email.trim().isEmpty()) {
            updateRequest.setEmail(email);
        }
        if (password != null && !password.trim().isEmpty()) {
            updateRequest.setPassword(password);
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            updateRequest.setProfileImage(profileImage);
        }

        Users users = usersRepository.findByLoginId(loginId)
            .orElseGet(() -> usersRepository.findByEmail(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
            ));

        Long userid = users.getUserId();

        // 이 줄도 수정 (currentPassword 파라미터 추가)
        myPageServiceSH.updateUserProfile(userid, updateRequest, currentPassword);

        return ResponseEntity.ok().build();
    }
}
