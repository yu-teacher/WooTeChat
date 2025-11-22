package com.woowa.wootechat.controller;

import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.dto.response.RoomInfoResponse;
import com.woowa.wootechat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 방 목록 조회
     */
    @GetMapping
    public List<RoomInfoResponse> getRooms() {
        return chatRoomService.findAllRooms().stream()
                .map(RoomInfoResponse::from)
                .toList();
    }

    /**
     * 방 생성
     */
    @PostMapping
    public RoomInfoResponse createRoom(@RequestParam("name") String name) {
        ChatRoom room = chatRoomService.createRoom(name);
        return RoomInfoResponse.from(room);
    }

    /**
     * 특정 방 조회
     */
    @GetMapping("/{roomId}")
    public RoomInfoResponse getRoom(@PathVariable String roomId) {
        ChatRoom room = chatRoomService.findRoomById(roomId);
        return RoomInfoResponse.from(room);
    }
}
