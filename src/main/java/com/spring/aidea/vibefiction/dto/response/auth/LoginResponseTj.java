package com.spring.aidea.vibefiction.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseTj {
    private String accessToken;
    private String message;
}
