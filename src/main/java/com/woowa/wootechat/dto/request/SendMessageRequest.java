package com.woowa.wootechat.dto.request;

import com.woowa.wootechat.domain.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지 전송 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
