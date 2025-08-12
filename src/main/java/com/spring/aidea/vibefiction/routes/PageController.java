package com.spring.aidea.vibefiction.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class PageController {

    // 홈으로 이동
    @GetMapping("/")
    public String home() {
        return "index";
    }
    // 챕터페이지로 이동
    @GetMapping("/chapters")
    public String chapters() {
        return "chapters-page";
    }
    // 투표페이지로 이동
    @GetMapping("/vote")
    public String vote() {
        return "vote-page";
    }
    @GetMapping("/novel")
    public String novel() {return "create-novel";}
    @GetMapping("/proposal")
    public String proposal() {return "create-proposal";}
    @GetMapping("/my-page")
    public String myPage() {return "my-page";}

}
