package com.woowa.wootechat.service;

import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.domain.room.RoomStatus;
import com.woowa.wootechat.dto.response.LobbyUpdateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        // 각 테스트마다 초기화
    }

    @Test
    void 방_생성() {
        ChatRoom room = chatRoomService.createRoom("테스트방");

        assertThat(room).isNotNull();
        assertThat(room.getRoomId()).isNotNull();
        assertThat(room.getRoomName()).isEqualTo("테스트방");
        assertThat(room.getUserCount()).isZero();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);

        // 로비에 브로드캐스트 확인
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/lobby"), any(LobbyUpdateMessage.class));
    }

    @Test
    void 전체_방_목록_조회() {
        chatRoomService.createRoom("방1");
        chatRoomService.createRoom("방2");
        chatRoomService.createRoom("방3");

        List<ChatRoom> rooms = chatRoomService.findAllRooms();

        assertThat(rooms).hasSize(3);
    }

    @Test
    void ACTIVE_방만_조회됨() {
        ChatRoom room1 = chatRoomService.createRoom("방1");
        ChatRoom room2 = chatRoomService.createRoom("방2");

        // room1에 사용자 입장 후 퇴장 (CLOSED)
        chatRoomService.addUserToRoom(room1.getRoomId(), "user1");
        chatRoomService.removeUserFromRoom(room1.getRoomId(), "user1");

        List<ChatRoom> rooms = chatRoomService.findAllRooms();

        // room1은 삭제되어서 room2만 남음
        assertThat(rooms).hasSize(1);
        assertThat(rooms.get(0).getRoomId()).isEqualTo(room2.getRoomId());
    }

    @Test
    void 특정_방_조회() {
        ChatRoom created = chatRoomService.createRoom("테스트방");

        ChatRoom found = chatRoomService.findRoomById(created.getRoomId());

        assertThat(found).isNotNull();
        assertThat(found.getRoomId()).isEqualTo(created.getRoomId());
        assertThat(found.getRoomName()).isEqualTo("테스트방");
    }

    @Test
    void 존재하지_않는_방_조회시_예외() {
        assertThatThrownBy(() -> chatRoomService.findRoomById("unknown-room"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("방을 찾을 수 없습니다");
    }

    @Test
    void 방_삭제() {
        ChatRoom room = chatRoomService.createRoom("테스트방");

        chatRoomService.deleteRoom(room.getRoomId());

        assertThatThrownBy(() -> chatRoomService.findRoomById(room.getRoomId()))
                .isInstanceOf(IllegalArgumentException.class);

        // 로비에 삭제 브로드캐스트 확인 (생성 1회 + 삭제 1회)
        verify(messagingTemplate, times(2))
                .convertAndSend(eq("/topic/lobby"), any(LobbyUpdateMessage.class));
    }

    @Test
    void 사용자_입장() {
        ChatRoom room = chatRoomService.createRoom("테스트방");

        chatRoomService.addUserToRoom(room.getRoomId(), "user1");

        ChatRoom found = chatRoomService.findRoomById(room.getRoomId());
        assertThat(found.getUserCount()).isEqualTo(1);

        // 방에 입장 메시지 브로드캐스트 확인
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/chat/room/" + room.getRoomId()), any(Object.class));
    }

    @Test
    void 여러_사용자_입장() {
        ChatRoom room = chatRoomService.createRoom("테스트방");

        chatRoomService.addUserToRoom(room.getRoomId(), "user1");
        chatRoomService.addUserToRoom(room.getRoomId(), "user2");
        chatRoomService.addUserToRoom(room.getRoomId(), "user3");

        ChatRoom found = chatRoomService.findRoomById(room.getRoomId());
        assertThat(found.getUserCount()).isEqualTo(3);
    }

    @Test
    void 사용자_퇴장() {
        ChatRoom room = chatRoomService.createRoom("테스트방");
        chatRoomService.addUserToRoom(room.getRoomId(), "user1");
        chatRoomService.addUserToRoom(room.getRoomId(), "user2");

        chatRoomService.removeUserFromRoom(room.getRoomId(), "user1");

        ChatRoom found = chatRoomService.findRoomById(room.getRoomId());
        assertThat(found.getUserCount()).isEqualTo(1);

        // 퇴장 메시지 브로드캐스트 확인
        verify(messagingTemplate, atLeastOnce())
                .convertAndSend(eq("/topic/chat/room/" + room.getRoomId()), any(Object.class));
    }

    @Test
    void 마지막_사용자_퇴장시_방_자동_삭제() {
        ChatRoom room = chatRoomService.createRoom("테스트방");
        chatRoomService.addUserToRoom(room.getRoomId(), "user1");

        chatRoomService.removeUserFromRoom(room.getRoomId(), "user1");

        // 방이 삭제되어 조회 불가
        assertThatThrownBy(() -> chatRoomService.findRoomById(room.getRoomId()))
                .isInstanceOf(IllegalArgumentException.class);

        // 로비에 삭제 알림 확인 - 타입 명시
        verify(messagingTemplate, atLeastOnce()).convertAndSend(
                eq("/topic/lobby"),
                (LobbyUpdateMessage) argThat((ArgumentMatcher<LobbyUpdateMessage>) msg ->
                        "DELETED".equals(msg.getType())
                )
        );
    }

    @Test
    void 연결_끊김_처리() {
        ChatRoom room = chatRoomService.createRoom("테스트방");
        chatRoomService.addUserToRoom(room.getRoomId(), "user1");
        chatRoomService.addUserToRoom(room.getRoomId(), "user2");

        chatRoomService.handleDisconnect(room.getRoomId(), "user1");

        ChatRoom found = chatRoomService.findRoomById(room.getRoomId());
        assertThat(found.getUserCount()).isEqualTo(1);
    }

    @Test
    void 연결_끊김시_마지막_사용자면_방_삭제() {
        ChatRoom room = chatRoomService.createRoom("테스트방");
        chatRoomService.addUserToRoom(room.getRoomId(), "user1");

        chatRoomService.handleDisconnect(room.getRoomId(), "user1");

        assertThatThrownBy(() -> chatRoomService.findRoomById(room.getRoomId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_방에_연결_끊김_처리시_예외_없음() {
        assertThatCode(() -> chatRoomService.handleDisconnect("unknown-room", "user1"))
                .doesNotThrowAnyException();
    }
}