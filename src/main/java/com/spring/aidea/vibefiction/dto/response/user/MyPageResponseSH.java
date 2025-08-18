package com.spring.aidea.vibefiction.dto.response.user;


import com.spring.aidea.vibefiction.dto.response.novel.NovelsResponseDtoSH;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Users;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseSH {

    private Long userid;
    private String loginId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private String role;
    private List<NovelsResponseDtoSH> novels;


    public static MyPageResponseSH from(Users user, List<Novels> novels) {

        return MyPageResponseSH.builder()
            .userid(user.getUserId())
            .loginId(user.getLoginId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .birthday(user.getBirthDate())
            .createdAt(user.getCreatedAt())
            .role(user.getRole().name())
            .novels(
                novels.stream()
                    .map(NovelsResponseDtoSH::from)
                    .toList()
            )
            .build();


    }





}
