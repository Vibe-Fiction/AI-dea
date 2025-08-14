package com.spring.aidea.vibefiction.dto.response.user;

import com.spring.aidea.vibefiction.entity.Users;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원가입 후 또는 마이페이지에서 렌더링에 사용할 JSON 응답 객체
 * @author 고동현
 */
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponseKO {

    private Long userid;
    private String loginId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private String role;


    /**
     * Users 엔터티를 UserResponseDto로 변환
     */
    public static UserResponseKO from(Users users) {
        return UserResponseKO.builder()
                .userid(users.getUserId())
                .loginId(users.getLoginId())
                .email(users.getEmail())
                .nickname(users.getNickname())
                .profileImageUrl(users.getProfileImageUrl())
                .birthday(users.getBirthDate())
                .createdAt(users.getCreatedAt())
                .role(users.getRole().name())
                .build();
    }


}
