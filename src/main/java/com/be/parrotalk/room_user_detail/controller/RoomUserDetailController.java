package com.be.parrotalk.room_user_detail.controller;

import com.be.parrotalk.login.security.JwtTokenProvider;
import com.be.parrotalk.room_user_detail.dto.RoomUserDetailResponse;
import com.be.parrotalk.room_user_detail.service.RoomUserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/details")
@RequiredArgsConstructor
public class RoomUserDetailController {

    private final RoomUserDetailService roomUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<List<RoomUserDetailResponse>> getRoomDetailsByUserId(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        String userId = jwtTokenProvider.getUserId(accessToken);
        List<RoomUserDetailResponse> details = roomUserDetailService.getDetailsByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(details);
    }

    @DeleteMapping("/{talkId}")
    public ResponseEntity<Void> deleteRoomDetail(@PathVariable Long talkId, HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        String userId = jwtTokenProvider.getUserId(accessToken);

        log.info("Deleting room detail with talkId: {} for userId: {}", talkId, userId);

        roomUserDetailService.deleteRoomDetailById(Long.parseLong(userId), talkId);

        return ResponseEntity.noContent().build();
    }
}
