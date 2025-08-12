package com.spring.aidea.vibefiction.controller;

import com.spring.aidea.vibefiction.dto.request.proposal.ProposalCreateRequestTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalCreateResponseTj;
import com.spring.aidea.vibefiction.dto.response.proposal.ProposalSummaryResponseTj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tj/chapters/{chapterId}/proposals")
public class ProposalControllerTj {

    @PostMapping
    public ResponseEntity<ProposalCreateResponseTj> create(@PathVariable Long chapterId,
                                                           @Valid @RequestBody ProposalCreateRequestTj req) {
        // TODO(TJ): Jwt → proposerId 추출 후 ProposalServiceTj.create(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<List<ProposalSummaryResponseTj>> list(@PathVariable Long chapterId) {
        // TODO(TJ): ProposalServiceTj.list(...) 연결
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
