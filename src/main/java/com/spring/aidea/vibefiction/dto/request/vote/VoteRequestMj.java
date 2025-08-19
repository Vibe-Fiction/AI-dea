package com.spring.aidea.vibefiction.dto.request.vote;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequestMj {
    private Long proposalId;
}
