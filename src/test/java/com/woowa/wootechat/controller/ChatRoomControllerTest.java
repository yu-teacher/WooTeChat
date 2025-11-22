package com.woowa.wootechat.controller;

import com.woowa.wootechat.domain.room.ChatRoom;
import com.woowa.wootechat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatRoomService chatRoomService;

    @Test
    void 방_목록_조회() throws Exception {
        ChatRoom room1 = new ChatRoom("room-1", "방1");
        ChatRoom room2 = new ChatRoom("room-2", "방2");
        when(chatRoomService.findAllRooms()).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomName").value("방1"))
                .andExpect(jsonPath("$[1].roomName").value("방2"));
    }

    @Test
    void 방_생성() throws Exception {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        when(chatRoomService.createRoom(any())).thenReturn(room);

        mockMvc.perform(post("/api/rooms")
                        .param("name", "테스트방"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value("room-1"))
                .andExpect(jsonPath("$.roomName").value("테스트방"))
                .andExpect(jsonPath("$.userCount").value(0));
    }

    @Test
    void 특정_방_조회() throws Exception {
        ChatRoom room = new ChatRoom("room-1", "테스트방");
        when(chatRoomService.findRoomById("room-1")).thenReturn(room);

        mockMvc.perform(get("/api/rooms/room-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value("room-1"))
                .andExpect(jsonPath("$.roomName").value("테스트방"));
    }

    @Test
    void 존재하지_않는_방_조회시_예외() throws Exception {
        when(chatRoomService.findRoomById("unknown"))
                .thenThrow(new IllegalArgumentException("[ERROR] 방을 찾을 수 없습니다: unknown"));

        mockMvc.perform(get("/api/rooms/unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 방을 찾을 수 없습니다: unknown"));
    }
}