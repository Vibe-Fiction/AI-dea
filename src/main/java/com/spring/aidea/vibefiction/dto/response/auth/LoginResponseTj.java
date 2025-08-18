/*
package com.spring.aidea.vibefiction.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

*/
/**
 * 사용자 로그인 인증에 성공했을 때 클라이언트에게 반환되는 데이터 전송 객체(DTO)입니다.
 *
 * 이 객체는 서비스의 보호된 리소스에 접근하는 데 필요한 JWT(액세스 토큰)를
 * 전달하는 핵심적인 역할을 합니다.
 * <p>
 * <b>[클라이언트 처리 지침]</b>
 * <ul>
 *   <li><b>사용법:</b> 클라이언트는 이 응답으로 받은 {@code accessToken}을 이후의 모든 API 요청 시
 *       HTTP 헤더의 {@code Authorization} 필드에 'Bearer {token}' 형식으로 포함하여 전송해야 합니다.</li>
 *   <li><b>보안:</b> 액세스 토큰은 민감 정보이므로, 클라이언트 측에서는 안전한 저장소
 *       (예: Secure Storage, Encrypted SharedPreferences)에 보관하고, 절대 로그에 기록해서는 안 됩니다.</li>
 * </ul>
 *
 * @author 왕택준
 * @since 2025.08
 *//*

@Getter
@AllArgsConstructor
public class LoginResponseTj {

    */
/**
     * 인증 성공 후 발급된 JWT(Json Web Token) 기반의 액세스 토큰입니다.
     * <p>
     * <b>[핵심 데이터]</b> 이 토큰은 사용자의 신원을 증명하고, 보호된 API 엔드포인트에 대한
     * 접근 권한을 부여하는 데 사용됩니다.
     *//*

    private final String accessToken;

    */
/**
     * 로그인 성공과 관련된 안내 메시지입니다.
     * <p>
     * <b>[사용 예시]</b> "로그인에 성공하였습니다." 와 같은 메시지를 담아
     * 클라이언트의 UI에 피드백을 제공하는 용도로 사용할 수 있습니다.
     *//*

    private final String message;
}
*/
