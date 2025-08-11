package com.spring.aidea.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 비밀 키 생성을 위한 테스트 클래스입니다.
 * @author 고동현
 */
public class SecretKeyGenKO {

      @Test
          @DisplayName("서버 비밀 키 생성 테스트")
          void generateKey() {

          // 32byte 키 생성
          SecureRandom secureRandom = new SecureRandom();
          byte[] key = new byte[32];
          secureRandom.nextBytes(key);

          // Base64 인코딩
          String secretKey = Base64.getEncoder().encodeToString(key);
          System.out.println("secretKey = " + secretKey);

      }
}
