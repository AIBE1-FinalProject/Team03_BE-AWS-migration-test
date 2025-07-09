package com.team03.ticketmon.queue.controller;

import com.team03.ticketmon._global.exception.SuccessResponse;
import com.team03.ticketmon.queue.dto.QueueStatusDto;
import com.team03.ticketmon.queue.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.team03.ticketmon.auth.jwt.CustomUserDetails; // CustomUserDetails 임포트 필요

/**
 * 대기열 시스템의 API 엔드포인트를 제공하는 컨트롤러입니다.
 * 클라이언트는 이 컨트롤러를 통해 대기열에 진입을 요청할 수 있습니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;

    /**
     * [POST /api/queue/enter]
     * 사용자를 특정 콘서트의 대기열에 등록
     *
     * @param concertId 클라이언트로부터 받은 콘서트 ID
     * @param user Spring Security가 JWT 토큰을 바탕으로 주입해주는 인증된 사용자 정보
     * @return HTTP 200 OK와 함께 대기열 상태 정보
     */
    @PostMapping("/enter")
    public ResponseEntity<SuccessResponse<QueueStatusDto>> enterQueue(
            @RequestParam Long concertId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        log.debug("[userId: {}] 대기열 진입 요청 수신. 콘서트 ID: {}", user.getUserId(), concertId);

        QueueStatusDto response = waitingQueueService.apply(concertId, user.getUserId());

        log.debug("[userId: {}] 대기열 진입 처리 완료. 결과 상태: {}", user.getUserId(), response.status());
        return ResponseEntity.ok(SuccessResponse.of("대기열 진입 처리 완료", response));
    }

    @GetMapping("/status")
    public ResponseEntity<SuccessResponse<QueueStatusDto>> getQueueStatus(
            @RequestParam Long concertId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.debug("[userId: {}] 대기열 상태 polling용 수신. 콘서트 ID: {}", user.getUserId(), concertId);

        QueueStatusDto response = waitingQueueService.getUserStatus(concertId, user.getUserId());
        log.debug("[userId: {}] 대기열 상태 polling용 응답. 결과 상태: {}", user.getUserId(), response.status());
        return ResponseEntity.ok(SuccessResponse.of("", response));
    }
}