package com.spring.aidea.vibefiction.controller;

// import 문에 UserDetails 추가
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
// ... 기타 import

@RestController
@RequestMapping("/api/my-page")
@Slf4j
@RequiredArgsConstructor
public class MyPageControllerSH {

    public final MyPageServiceSH myPageServiceSH;
    public final UsersRepository usersRepository;

    @GetMapping


    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetails userDetails){


        String loginId = userDetails.getUsername();

        Users users = usersRepository.findByLoginId(loginId)
            .orElseGet(() -> usersRepository.findByEmail(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
            ));

        Long userid = users.getUserId();

        MyPageResponseSH userAndNovels = myPageServiceSH.findUserAndNovelsById(userid);

        return ResponseEntity.ok(userAndNovels);
    }

    @PostMapping
    public ResponseEntity<?> updateProfile(

        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) String currentPassword,
        @RequestParam(required = false) MultipartFile profileImage
    ) {

        String loginId = userDetails.getUsername();

        UserUpdateRequestSH updateRequest = new UserUpdateRequestSH();


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

        myPageServiceSH.updateUserProfile(userid, updateRequest, currentPassword);

        return ResponseEntity.ok().build();
    }
}
