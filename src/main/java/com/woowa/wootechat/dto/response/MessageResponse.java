package com.woowa.wootechat.dto.response;

import com.woowa.wootechat.domain.message.ChatMessage;
import com.woowa.wootechat.domain.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 메시지 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private Instant timestamp;

    /**
     * ChatMessage 도메인 → DTO 변환
     */
    public static MessageResponse from(ChatMessage chatMessage) {
        return new MessageResponse(
                chatMessage.getType(),
                chatMessage.getRoomId(),
                chatMessage.getSender(),
                chatMessage.getContent(),
                chatMessage.getTimestamp()
        );
    }
}
