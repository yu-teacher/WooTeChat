package com.woowa.wootechat.domain.message;

import lombok.Getter;
import java.time.Instant;

/**
 * 채팅 메시지 도메인 엔티티
 */
@Getter
public class ChatMessage {

    private final MessageType type;
    private final String roomId;
    private final String sender;
    private final String content;
    private final Instant timestamp;

    private ChatMessage(MessageType type, String roomId, String sender, String content) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.timestamp = Instant.now();
    }

    /**
     * 입장 메시지 생성
     */
    public static ChatMessage enter(String roomId, String sender) {
        return new ChatMessage(
                MessageType.ENTER,
                roomId,
                sender,
                sender + "님이 입장하셨습니다."
        );
    }

    /**
     * 대화 메시지 생성
     */
    public static ChatMessage talk(String roomId, String sender, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 메시지 내용은 필수입니다.");
        }
        return new ChatMessage(MessageType.TALK, roomId, sender, content);
    }

    /**
     * 퇴장 메시지 생성
     */
    public static ChatMessage leave(String roomId, String sender) {
        return new ChatMessage(
                MessageType.LEAVE,
                roomId,
                sender,
                sender + "님이 퇴장하셨습니다."
        );
    }
}