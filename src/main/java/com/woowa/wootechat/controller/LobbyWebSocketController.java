package com.woowa.wootechat.controller;

import com.woowa.wootechat.dto.response.RoomInfoResponse;
import com.woowa.wootechat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 로비 WebSocket 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class LobbyWebSocketController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 로비 구독 (초기 방 목록 전송)
     * /app/lobby.subscribe
     */
    @MessageMapping("/lobby.subscribe")
    public void subscribeLobby() {
        List<RoomInfoResponse> rooms = chatRoomService.findAllRooms().stream()
                .map(RoomInfoResponse::from)
                .toList();

        messagingTemplate.convertAndSend("/topic/lobby", rooms);
    }
}
