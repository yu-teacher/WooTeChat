package com.woowa.wootechat.service;

import com.woowa.wootechat.domain.message.ChatMessage;
import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.domain.room.RoomStatus;
import com.woowa.wootechat.dto.response.LobbyUpdateMessage;
import com.woowa.wootechat.dto.response.MessageResponse;
import com.woowa.wootechat.dto.response.RoomInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅방 서비스
 */
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방 생성
     */
    public ChatRoom createRoom(String name) {
        String roomId = UUID.randomUUID().toString();
        ChatRoom room = new ChatRoom(roomId, name);
        rooms.put(roomId, room);

        // 로비에 방 생성 알림
        broadcastToLobby("CREATED", RoomInfoResponse.from(room));

        return room;
    }

    /**
     * 전체 방 목록 조회 (ACTIVE만)
     */
    public List<ChatRoom> findAllRooms() {
        return rooms.values().stream()
                .filter(room -> room.getStatus() == RoomStatus.ACTIVE)
                .toList();
    }

    /**
     * 특정 방 조회
     */
    public ChatRoom findRoomById(String roomId) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("[ERROR] 방을 찾을 수 없습니다: " + roomId);
        }
        return room;
    }

    /**
     * 방 삭제
     */
    public void deleteRoom(String roomId) {
        ChatRoom room = rooms.remove(roomId);
        if (room != null) {
            // 로비에 방 삭제 알림
            broadcastToLobby("DELETED", roomId);
        }
    }

    /**
     * 사용자 입장
     */
    public void addUserToRoom(String roomId, String username) {
        ChatRoom room = findRoomById(roomId);
        room.addUser(username);

        // 입장 메시지 브로드캐스트
        ChatMessage enterMessage = ChatMessage.enter(roomId, username);
        broadcastToRoom(roomId, enterMessage);
    }

    /**
     * 사용자 퇴장
     */
    public void removeUserFromRoom(String roomId, String username) {
        ChatRoom room = findRoomById(roomId);
        room.removeUser(username);

        // 퇴장 메시지 브로드캐스트
        ChatMessage leaveMessage = ChatMessage.leave(roomId, username);
        broadcastToRoom(roomId, leaveMessage);

        // 방이 비었으면 삭제
        if (room.isEmpty()) {
            deleteRoom(roomId);
        }
    }

    /**
     * 연결 끊김 처리
     */
    public void handleDisconnect(String roomId, String username) {
        try {
            removeUserFromRoom(roomId, username);
        } catch (Exception e) {
            // 이미 삭제된 방이거나 존재하지 않는 사용자일 수 있음
            // 로그만 남기고 예외는 무시
            System.err.println("연결 끊김 처리 중 오류: " + e.getMessage());
        }
    }

    /**
     * 방에 메시지 브로드캐스트
     */
    private void broadcastToRoom(String roomId, ChatMessage message) {
        MessageResponse response = MessageResponse.from(message);
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, response);
    }

    /**
     * 로비에 브로드캐스트
     */
    private void broadcastToLobby(String type, Object data) {
        LobbyUpdateMessage message = new LobbyUpdateMessage(type, data);
        messagingTemplate.convertAndSend("/topic/lobby", message);
    }
}
