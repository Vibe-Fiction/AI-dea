package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.response.user.MyPageResponseSH;
import com.spring.aidea.vibefiction.service.MyPageServiceSH;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-page")
@Slf4j
@RequiredArgsConstructor
public class MyPageControllerSH {

    public final MyPageServiceSH myPageServiceSH;

    @GetMapping
    public ResponseEntity<?> getMyPage(@RequestParam Long userid){


        MyPageResponseSH userAndNovels = myPageServiceSH.findUserAndNovelsById(userid);


        return ResponseEntity.ok(userAndNovels);
    }


}
