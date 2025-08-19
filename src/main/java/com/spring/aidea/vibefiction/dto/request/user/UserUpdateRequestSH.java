package com.spring.aidea.vibefiction.dto.request.user;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestSH {
    private Long userid;
    private String nickname;
    private String email;
    private String password;
    private MultipartFile profileImage;

    // 정적 팩토리 메서드
    public static UserUpdateRequestSH from(Long userid, String nickname,
                                           String email, String password,
                                           MultipartFile profileImage) {
        UserUpdateRequestSH request = new UserUpdateRequestSH();
        request.userid = userid;
        request.nickname = nickname;
        request.email = email;
        request.password = password;
        request.profileImage = profileImage;
        return request;
    }

}
