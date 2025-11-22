package com.woowa.wootechat.controller;

import com.woowa.wootechat.domain.message.ChatMessage;
import com.woowa.wootechat.domain.message.MessageType;
import com.woowa.wootechat.dto.request.SendMessageRequest;
import com.woowa.wootechat.dto.response.MessageResponse;
import com.woowa.wootechat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 채팅 WebSocket 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     * /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(SendMessageRequest request) {
        String roomId = request.getRoomId();
        String sender = request.getSender();
        MessageType type = request.getType();

        ChatMessage message;

        if (MessageType.ENTER.equals(type)) {
            // 입장 처리
            chatRoomService.addUserToRoom(roomId, sender);
            return; // addUserToRoom에서 이미 브로드캐스트함

        } else if (MessageType.LEAVE.equals(type)) {
            // 퇴장 처리
            chatRoomService.removeUserFromRoom(roomId, sender);
            return; // removeUserFromRoom에서 이미 브로드캐스트함

        } else if (MessageType.TALK.equals(type)) {
            // 대화 메시지
            String content = request.getMessage();
            message = ChatMessage.talk(roomId, sender, content);

        } else {
            throw new IllegalArgumentException("[ERROR] 알 수 없는 메시지 타입: " + type);
        }

        // 메시지 브로드캐스트
        MessageResponse response = MessageResponse.from(message);
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, response);
    }
}
