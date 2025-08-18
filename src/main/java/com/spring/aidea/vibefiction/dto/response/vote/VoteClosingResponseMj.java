package com.spring.aidea.vibefiction.dto.response.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteClosingResponseMj {
    private Long chapterId;
    private String closingTime;
}
