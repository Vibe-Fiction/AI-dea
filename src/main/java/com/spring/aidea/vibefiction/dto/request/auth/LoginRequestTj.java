package com.spring.aidea.vibefiction.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 로그인을 위해 아이디와 비밀번호를 전달하는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 클라이언트로부터 서버로 사용자 인증 정보를 전달하는 데 사용됩니다.
 * <p>
 * <b>[보안 지침]</b>
 * <ul>
 *   <li>이 DTO는 민감정보(비밀번호)를 포함하므로, 반드시 <b>HTTPS/TLS</b> 프로토콜을 통해 암호화되어 전송되어야 합니다.</li>
 *   <li>서버 또는 클라이언트 측 로그에 이 객체의 내용, 특히 비밀번호 필드를 평문으로 기록해서는 안 됩니다. (e.g., toString() 재정의 시 주의)</li>
 * </ul>
 *
 * @author 왕택준
 * @since 2025.08
 */
@Getter
@Setter
public class LoginRequestTj {

    /**
     * 사용자의 로그인 계정 ID입니다.
     * <p>
     * <b>[비즈니스 규칙]</b> 사용자를 고유하게 식별하는 값으로, 비어 있을 수 없습니다.
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String loginId;

    /**
     * 사용자의 로그인 비밀번호입니다. (평문 형태)
     * <p>
     * <b>[비즈니스 규칙]</b> 비밀번호는 비어 있을 수 없으며, 서버에서는 이 값을 해시(Hash) 처리하여
     * 저장된 비밀번호와 비교합니다.
     * <p>
     * <b>[보안 경고]</b> 이 필드는 전송 계층(HTTPS)에서만 암호화됩니다.
     * 절대로 로그나 다른 곳에 평문으로 저장하거나 노출해서는 안 됩니다.
     *
     * @see jakarta.validation.constraints.NotBlank
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
