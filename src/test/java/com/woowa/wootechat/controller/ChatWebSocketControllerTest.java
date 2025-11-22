package com.woowa.wootechat.controller;

import com.woowa.wootechat.domain.message.MessageType;
import com.woowa.wootechat.dto.request.SendMessageRequest;
import com.woowa.wootechat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatWebSocketController controller;

    @Test
    void ENTER_메시지_처리() {
        SendMessageRequest request = new SendMessageRequest(
                MessageType.ENTER, "room-1", "user1", null
        );

        controller.sendMessage(request);

        verify(chatRoomService, times(1)).addUserToRoom("room-1", "user1");
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void LEAVE_메시지_처리() {
        SendMessageRequest request = new SendMessageRequest(
                MessageType.LEAVE, "room-1", "user1", null
        );

        controller.sendMessage(request);

        verify(chatRoomService, times(1)).removeUserFromRoom("room-1", "user1");
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void TALK_메시지_처리() {
        SendMessageRequest request = new SendMessageRequest(
                MessageType.TALK, "room-1", "user1", "안녕하세요"
        );

        controller.sendMessage(request);

        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/chat/room/room-1"), any(Object.class));
    }
}
