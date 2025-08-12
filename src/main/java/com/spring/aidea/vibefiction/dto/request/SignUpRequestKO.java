package com.spring.aidea.vibefiction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

/**
 * 회원 가입 요청에 사용하는 DTO입니다.
 *
 * @author 고동현
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpRequestKO {

    @NotBlank(message = "사용자명은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,15}$", message = "사용자명은 3~15자의 영문, 숫자, 언더스코어만 사용 가능합니다.")
    private String loginId;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern( regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해야 합니다.")
    private String password;

    @NotBlank(message = "사용할 작가명은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "닉네임은 2~10자의 영문, 숫자, 한글만 사용 가능합니다.")
    private String nickname;

    @NotNull(message = "생년월일을 입력해주세요.")
    @Past(message = "생년월일은 현재 날짜보다 이전이어야 합니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDate;


}
