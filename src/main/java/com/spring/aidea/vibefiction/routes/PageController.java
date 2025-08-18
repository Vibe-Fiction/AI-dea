package com.spring.aidea.vibefiction.routes;

import com.spring.aidea.vibefiction.entity.Chapters;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.repository.ChaptersRepository;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PageController {

    /**
     * 회차 정보를 데이터베이스에서 조회하기 위한 Repository입니다.
     * {@code @RequiredArgsConstructor}에 의해 생성자 주입 방식으로 의존성이 주입됩니다.
     */
    private final ChaptersRepository chaptersRepository;

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

    /**
     * '투표하기' 페이지로 이동합니다.
     * @return "vote-page" 템플릿 이름
     */
    @GetMapping("/vote-page/{novelId}")
    public String votePage(@PathVariable Long novelId, Model model) {
        model.addAttribute("novelId", novelId);
        return "vote-page";
    }
    // 소설페이지로 이동
    @GetMapping("/novel")
    public String novel() {return "create-novel";}

    /**
     * '새 소설 쓰기' 페이지로 이동합니다.
     * @return "create-novel" 템플릿 이름
     */
    @GetMapping("/novels/create")
    public String novelCreatePage() {
        return "create-novel";
    }

    /**
     * 특정 회차에 대한 '이어쓰기 제안' 페이지로 이동합니다.
     * @param chapterId 이어쓰기를 제안할 대상 회차의 ID.
     * @param model     Thymeleaf에 데이터를 전달할 모델 객체.
     * @return "create-proposal" 템플릿 이름
     */
    @GetMapping("/proposals/create/{chapterId}")
    public String proposalCreatePage(@PathVariable Long chapterId, Model model) {
        log.info("이어쓰기 페이지 요청. Chapter ID: {}", chapterId);
        Chapters chapter = chaptersRepository.findById(chapterId)
            .orElseThrow(() -> new NoSuchElementException("ID가 " + chapterId + "인 회차를 찾을 수 없습니다."));
        model.addAttribute("novelData", chapter.getNovel());
        return "create-proposal";
    }

    // 마이페이지로 이동
    @GetMapping("/my-page")
    public String myPage() {return "my-page";}
    // 회원가입 페이지로 이동
    @GetMapping("/signup")
    public String signUp() {return "signup";}
}
