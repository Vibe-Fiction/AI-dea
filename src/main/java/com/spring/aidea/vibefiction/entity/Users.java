package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "users")
@Getter
@EqualsAndHashCode(of = "userId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"novels", "collaborators", "proposals", "chapters", "votes", "favorites", "aiInteractionLogs"})
@Comment("회원 테이블")
public class Users {

    // 기본 키 user_id, BIGINT AUTO_INCREMENT에 해당
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", columnDefinition = "BIGINT")
    @Comment("사용자 ID")
    private Long userId;

    // 로그인 ID, VARCHAR(50) NOT NULL, UNIQUE에 해당
    @Column(name = "login_id", length = 50, nullable = false, unique = true)
    @Comment("로그인 ID")
    private String loginId;

    // 비밀번호, VARCHAR(255) NOT NULL
    @Column(name = "password", length = 255, nullable = false)
    @Comment("암호화된 비밀번호(bcrypt)")
    private String password;

    // 닉네임, VARCHAR(50) NOT NULL, UNIQUE에 해당
    @Column(name = "nickname", length = 50, nullable = false, unique = true)
    @Comment("닉네임")
    private String nickname;

    // 이메일, VARCHAR(50) NOT NULL, UNIQUE에 해당
    @Column(name = "email", length = 50, nullable = false, unique = true)
    @Comment("이메일 주소(인증/알림용)")
    private String email;

    // 생년월일, DATE NOT NULL
    @Column(name = "birth_date", nullable = false)
    @Comment("생년월일(연령제한용)")
    private LocalDate birthDate;


    // 프로필 이미지 URL, VARCHAR(255) NULL
    @Column(name = "profile_image_url", length = 255)
    @Comment("프로필 사진 이미지 경로")
    @Builder.Default
    private String profileImageUrl = "/img/default-profile.webp"; // 기본 프로필 이미지

    // 사용자 권한, ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER'
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Comment("사용자 권한")
    private Role role;

    // 계정 상태, ENUM('ACTIVE', 'BLOCKED', 'DEACTIVATED') NOT NULL DEFAULT 'ACTIVE'
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("계정 상태")
    private Status status;

    // 가입 일시, TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("가입일시")
    private LocalDateTime createdAt;

    public enum Role {
        USER, // 사용자
        ADMIN // 관리자
    }

    public enum Status {
        ACTIVE,
        BLOCKED,
        DEACTIVATED
    }

    //=========================
    // 연관관계 추가해야함
    // =========================

    @OneToMany(mappedBy = "author")
    @Builder.Default
    private List<Novels> novels = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Collaborators> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "proposer")
    @Builder.Default
    private List<Proposals> proposals = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    @Builder.Default
    private List<Chapters> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Votes> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorites> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<AiInteractionLogs> aiInteractionLogs = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) this.role = Role.USER;
        if (this.status == null) this.status = Status.ACTIVE;

    }
}
