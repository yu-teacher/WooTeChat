package com.woowa.wootechat.domain.room;

import lombok.Getter;
import java.time.Instant;

/**
 * 채팅방 도메인 엔티티
 */
@Getter
public class ChatRoom {

    private final String roomId;
    private final String roomName;
    private final Participants participants;
    private final Instant createdAt;
    private RoomStatus status;

    public ChatRoom(String roomId, String roomName) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 방 ID는 필수입니다.");
        }
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 방 이름은 필수입니다.");
        }

        this.roomId = roomId;
        this.roomName = roomName;
        this.participants = new Participants();
        this.createdAt = Instant.now();
        this.status = RoomStatus.ACTIVE;
    }

    /**
     * 사용자 입장
     */
    public void addUser(String username) {
        validateActive();
        participants.add(username);
    }

    /**
     * 사용자 퇴장
     */
    public void removeUser(String username) {
        participants.remove(username);

        // 0명이면 방 종료
        if (participants.isEmpty()) {
            this.status = RoomStatus.CLOSED;
        }
    }

    /**
     * 활성 상태 검증
     */
    private void validateActive() {
        if (status != RoomStatus.ACTIVE) {
            throw new IllegalStateException("[ERROR] 방이 종료되었습니다.");
        }
    }

    /**
     * 방이 비었는지
     */
    public boolean isEmpty() {
        return participants.isEmpty();
    }

    /**
     * 종료되었는지
     */
    public boolean isClosed() {
        return status == RoomStatus.CLOSED;
    }

    /**
     * 참가자 수 조회
     */
    public int getUserCount() {
        return participants.getCount();
    }
}