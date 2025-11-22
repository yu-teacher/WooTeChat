package com.woowa.wootechat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로비 업데이트 메시지
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LobbyUpdateMessage {
    private String type;  // CREATED, DELETED
    private Object data;  // ChatRoom 또는 roomId
}
