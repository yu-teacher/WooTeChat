package com.woowa.wootechat.controller;

import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyWebSocketControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LobbyWebSocketController controller;

    @Test
    void 로비_구독시_방_목록_전송() {
        ChatRoom room1 = new ChatRoom("room-1", "방1");
        ChatRoom room2 = new ChatRoom("room-2", "방2");
        when(chatRoomService.findAllRooms()).thenReturn(List.of(room1, room2));

        controller.subscribeLobby();

        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/lobby"), any(List.class));
    }

    @Test
    void 방이_없으면_빈_목록_전송() {
        when(chatRoomService.findAllRooms()).thenReturn(List.of());

        controller.subscribeLobby();

        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/lobby"), any(List.class));
    }
}
