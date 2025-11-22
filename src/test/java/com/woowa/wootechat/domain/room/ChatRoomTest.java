package com.woowa.wootechat.domain.room;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ChatRoomTest {

    @Test
    void 방_생성시_초기_상태_확인() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");

        assertThat(room.getRoomId()).isEqualTo("room-1");
        assertThat(room.getRoomName()).isEqualTo("테스트방");
        assertThat(room.getUserCount()).isZero();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
        assertThat(room.isEmpty()).isTrue();
        assertThat(room.isClosed()).isFalse();
        assertThat(room.getCreatedAt()).isNotNull();
    }

    @Test
    void 방_ID가_null이면_예외() {
        assertThatThrownBy(() -> new ChatRoom(null, "테스트방"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("방 ID는 필수입니다");
    }

    @Test
    void 방_ID가_빈_문자열이면_예외() {
        assertThatThrownBy(() -> new ChatRoom("", "테스트방"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("방 ID는 필수입니다");
    }

    @Test
    void 방_이름이_null이면_예외() {
        assertThatThrownBy(() -> new ChatRoom("room-1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("방 이름은 필수입니다");
    }

    @Test
    void 방_이름이_빈_문자열이면_예외() {
        assertThatThrownBy(() -> new ChatRoom("room-1", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("방 이름은 필수입니다");
    }

    @Test
    void 사용자_입장() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");

        room.addUser("user1");

        assertThat(room.getUserCount()).isEqualTo(1);
        assertThat(room.isEmpty()).isFalse();
    }

    @Test
    void 여러_사용자_입장() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");

        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        assertThat(room.getUserCount()).isEqualTo(3);
    }

    @Test
    void 사용자_퇴장() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        room.addUser("user1");
        room.addUser("user2");

        room.removeUser("user1");

        assertThat(room.getUserCount()).isEqualTo(1);
        assertThat(room.isEmpty()).isFalse();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
    }

    @Test
    void 마지막_사용자_퇴장시_방_종료() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        room.addUser("user1");

        room.removeUser("user1");

        assertThat(room.getUserCount()).isZero();
        assertThat(room.isEmpty()).isTrue();
        assertThat(room.isClosed()).isTrue();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.CLOSED);
    }

    @Test
    void 모든_사용자_퇴장시_방_종료() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        room.removeUser("user1");
        room.removeUser("user2");
        room.removeUser("user3");

        assertThat(room.isEmpty()).isTrue();
        assertThat(room.isClosed()).isTrue();
    }

    @Test
    void 종료된_방에는_입장_불가() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        room.addUser("user1");
        room.removeUser("user1"); // 방 종료

        assertThatThrownBy(() -> room.addUser("user2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("방이 종료되었습니다");
    }

    @Test
    void 존재하지_않는_사용자_퇴장시_예외_없음() {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        room.addUser("user1");

        assertThatCode(() -> room.removeUser("user2"))
                .doesNotThrowAnyException();

        assertThat(room.getUserCount()).isEqualTo(1);
    }
}
