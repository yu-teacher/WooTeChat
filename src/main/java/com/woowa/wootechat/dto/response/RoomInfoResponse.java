package com.woowa.wootechat.dto.response;

import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.domain.room.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 방 정보 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoResponse {
    private String roomId;
    private String roomName;
    private int userCount;
    private RoomStatus status;

    /**
     * ChatRoom 도메인 → DTO 변환
     */
    public static RoomInfoResponse from(ChatRoom room) {
        return new RoomInfoResponse(
                room.getRoomId(),
                room.getRoomName(),
                room.getUserCount(),
                room.getStatus()
        );
    }
}
