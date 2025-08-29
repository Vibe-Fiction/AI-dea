package com.spring.aidea.vibefiction.global.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Getter
@Setter
public class FileUploadConfig {

    // yml의 설정을 읽는 어노테이션
    @Value("${file.upload.location}")
    private String location;

    @PostConstruct
    public void init() {
        File directory = new File(location);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
