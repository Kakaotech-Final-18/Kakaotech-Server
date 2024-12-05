package com.be.parrotalk.talk.controller;

import com.be.parrotalk.login.security.JwtTokenProvider;
import com.be.parrotalk.talk.dto.CreateTalkRequest;
import com.be.parrotalk.talk.dto.ReceiverInfoResponse;
import com.be.parrotalk.talk.dto.UpdateReceiverRequest;
import com.be.parrotalk.talk.service.TalkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/talk")
@RequiredArgsConstructor
public class TalkController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TalkService talkService;

    @PostMapping("/create")
    public ResponseEntity<Long> startCall(HttpServletRequest request, @RequestBody CreateTalkRequest createTalkRequest) {
        // Access Token 추출
        String accessToken = jwtTokenProvider.resolveToken(request);
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtTokenProvider.getUserId(accessToken);

        // Talk 생성 요청 처리 위임
        try {
            Long talkId = talkService.createTalk(userId, createTalkRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(talkId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/peer")
    public ResponseEntity<?> updateReceiver(@RequestBody UpdateReceiverRequest updateReceiverRequest) {
        try {
            ReceiverInfoResponse receiverInfo = talkService.updateReceiver(updateReceiverRequest);
            return ResponseEntity.ok(receiverInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.warn("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
