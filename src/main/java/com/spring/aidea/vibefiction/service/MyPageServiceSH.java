package com.spring.aidea.vibefiction.service;

import com.spring.aidea.vibefiction.dto.request.user.UserUpdateRequestSH;
import com.spring.aidea.vibefiction.dto.response.user.MyPageResponseSH;
import com.spring.aidea.vibefiction.entity.Novels;
import com.spring.aidea.vibefiction.entity.Users;
import com.spring.aidea.vibefiction.global.config.FileUploadConfig;
import com.spring.aidea.vibefiction.global.exception.BusinessException;
import com.spring.aidea.vibefiction.global.exception.ErrorCode;
import com.spring.aidea.vibefiction.repository.NovelsRepository;
import com.spring.aidea.vibefiction.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageServiceSH {

    private final NovelsRepository novelsRepository;
    private final UsersRepository usersRepository;
    private final FileUploadConfig fileUploadConfig;
    private final PasswordEncoder passwordEncoder; // 추가

    /**
     * MYPAGE 렌더링에 필요한 사용자의 정보와 사용자가 참여한 소설리스트를 반환하는 메서드
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

    /**
     * 사용자 프로필 정보를 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param updateRequest 업데이트할 정보
     * @param currentPassword 현재 비밀번호 (비밀번호 변경 시 필요)
     */
    public void updateUserProfile(Long userId, UserUpdateRequestSH updateRequest, String currentPassword) {
        // 사용자 조회
        Users user = usersRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 변경 요청이 있는 경우 현재 비밀번호 확인
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호 변경을 위해서는 현재 비밀번호가 필요합니다.");
            }

            // 현재 비밀번호 검증
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
        }

        String oldProfileImagePath = user.getProfileImageUrl(); // 기존 프로필 이미지 경로
        boolean isUpdated = false;

        // 업데이트할 값들을 준비 (기존 값 또는 새 값)
        String newNickname = user.getNickname();
        String newEmail = user.getEmail();
        String newPassword = user.getPassword();
        String newProfileImageUrl = user.getProfileImageUrl();

        // 닉네임 업데이트 체크
        if (updateRequest.getNickname() != null && !updateRequest.getNickname().trim().isEmpty()) {
            newNickname = updateRequest.getNickname().trim();
            isUpdated = true;
            log.info("닉네임 업데이트: 사용자ID {} -> {}", userId, newNickname);
        }

        // 이메일 업데이트 체크
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
            newEmail = updateRequest.getEmail().trim();
            isUpdated = true;
            log.info("이메일 업데이트: 사용자ID {}", userId);
        }

        // 비밀번호 업데이트 체크 (암호화 처리)
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
            // 새 비밀번호 암호화
            newPassword = passwordEncoder.encode(updateRequest.getPassword().trim());
            isUpdated = true;
            log.info("비밀번호 업데이트: 사용자ID {}", userId);
        }

        // 프로필 이미지 업데이트 체크
        if (updateRequest.getProfileImage() != null && !updateRequest.getProfileImage().isEmpty()) {
            try {
                // 새 프로필 이미지 저장
                String newProfileImagePath = saveProfileImage(updateRequest.getProfileImage(), userId);
                newProfileImageUrl = newProfileImagePath;
                isUpdated = true;
                log.info("프로필 이미지 업데이트: 사용자ID {} -> {}", userId, newProfileImagePath);

            } catch (Exception e) {
                log.error("프로필 이미지 업데이트 실패: 사용자ID {}", userId, e);
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        }

        // 변경사항이 있으면 새 객체 생성 후 DB 저장 (빌더 패턴)
        if (isUpdated) {
            Users updatedUser = Users.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .password(newPassword)
                .nickname(newNickname)
                .email(newEmail)
                .birthDate(user.getBirthDate())
                .profileImageUrl(newProfileImageUrl)
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                // 연관관계 필드들도 유지
                .novels(user.getNovels())
                .collaborators(user.getCollaborators())
                .proposals(user.getProposals())
                .chapters(user.getChapters())
                .votes(user.getVotes())
                .favorites(user.getFavorites())
                .aiInteractionLogs(user.getAiInteractionLogs())
                .build();

            usersRepository.save(updatedUser);
            log.info("사용자 정보 업데이트 완료: 사용자ID {}", userId);
        } else {
            log.info("업데이트할 정보가 없습니다: 사용자ID {}", userId);
        }
    }

    /**
     * 사용자 프로필 정보를 업데이트합니다. (비밀번호 변경 없는 경우)
     *
     * @param userId 사용자 ID
     * @param updateRequest 업데이트할 정보
     */
    public void updateUserProfile(Long userId, UserUpdateRequestSH updateRequest) {
        updateUserProfile(userId, updateRequest, null);
    }

    /**
     * 프로필 이미지를 로컬에 저장하고 저장된 파일의 절대경로를 반환합니다.
     *
     * @param file 업로드된 이미지 파일
     * @param userId 사용자 ID
     * @return 저장된 파일의 절대경로
     */
    private String saveProfileImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        validateImageFile(file);

        try {
            log.info("=== 파일 저장 시작 ===");
            log.info("파일 이름: {}", file.getOriginalFilename());
            log.info("파일 크기: {}", file.getSize());

            // 새 파일명 생성 (UUID + 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = "profile_" + UUID.randomUUID().toString() + extension;

            // 사용자별 폴더 경로 생성: uploads/1/ , uploads/2/ 형태
            Path uploadPath = Paths.get(fileUploadConfig.getLocation());
            Path userPath = uploadPath.resolve(userId.toString()); // 사용자 ID로 폴더 생성
            Path filePath = userPath.resolve(newFilename);

            log.info("사용자 폴더 경로: {}", userPath.toAbsolutePath());
            log.info("파일 저장 경로: {}", filePath.toAbsolutePath());

            // 사용자별 디렉토리 생성 (없을 경우)
            Files.createDirectories(userPath);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath);

            // 웹 URL 반환: /uploads/1/profile_uuid.jpg
            String webUrl = "/uploads/" + userId + "/" + newFilename;

            log.info("프로필 이미지 저장 완료 - 사용자ID: {}, 폴더: {}, 파일명: {}, URL: {}",
                userId, userPath.toAbsolutePath(), newFilename, webUrl);

            return webUrl;

        } catch (IOException e) {
            log.error("파일 저장 실패 - 사용자ID: {}, 파일명: {}", userId, file.getOriginalFilename(), e);
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    /**
     * 이미지 파일 유효성 검사
     *
     * @param file 검사할 파일
     */
    private void validateImageFile(MultipartFile file) {
        // 파일 크기 검사 (5MB 제한)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 형식 검사
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedImageType(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "지원하지 않는 파일 형식입니다. JPG, PNG, GIF, WEBP 파일만 업로드 가능합니다.");
        }

        // 파일명 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }
    }

    /**
     * 허용된 이미지 타입인지 확인
     *
     * @param contentType MIME 타입
     * @return 허용된 타입이면 true
     */
    private boolean isAllowedImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/webp");
    }

    /**
     * 파일 확장자 추출
     *
     * @param filename 파일명
     * @return 확장자 (점 포함)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".jpg"; // 기본 확장자
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
