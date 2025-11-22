package com.woowa.wootechat.domain.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ChatMessageTest {

    @Test
    void 입장_메시지_생성() {
        ChatMessage message = ChatMessage.enter("room-1", "user1");

        assertThat(message.getType()).isEqualTo(MessageType.ENTER);
        assertThat(message.getRoomId()).isEqualTo("room-1");
        assertThat(message.getSender()).isEqualTo("user1");
        assertThat(message.getContent()).isEqualTo("user1님이 입장하셨습니다.");
        assertThat(message.getTimestamp()).isNotNull();
    }

    @Test
    void 대화_메시지_생성() {
        ChatMessage message = ChatMessage.talk("room-1", "user1", "안녕하세요");

        assertThat(message.getType()).isEqualTo(MessageType.TALK);
        assertThat(message.getRoomId()).isEqualTo("room-1");
        assertThat(message.getSender()).isEqualTo("user1");
        assertThat(message.getContent()).isEqualTo("안녕하세요");
        assertThat(message.getTimestamp()).isNotNull();
    }

    @Test
    void 퇴장_메시지_생성() {
        ChatMessage message = ChatMessage.leave("room-1", "user1");

        assertThat(message.getType()).isEqualTo(MessageType.LEAVE);
        assertThat(message.getRoomId()).isEqualTo("room-1");
        assertThat(message.getSender()).isEqualTo("user1");
        assertThat(message.getContent()).isEqualTo("user1님이 퇴장하셨습니다.");
        assertThat(message.getTimestamp()).isNotNull();
    }

    @Test
    void 대화_메시지_내용이_null이면_예외() {
        assertThatThrownBy(() -> ChatMessage.talk("room-1", "user1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메시지 내용은 필수입니다");
    }

    @Test
    void 대화_메시지_내용이_빈_문자열이면_예외() {
        assertThatThrownBy(() -> ChatMessage.talk("room-1", "user1", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메시지 내용은 필수입니다");
    }

    @Test
    void 대화_메시지_내용이_공백이면_예외() {
        assertThatThrownBy(() -> ChatMessage.talk("room-1", "user1", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메시지 내용은 필수입니다");
    }

    @Test
    void 여러_메시지는_다른_타임스탬프를_가짐() throws InterruptedException {
        ChatMessage message1 = ChatMessage.talk("room-1", "user1", "첫 번째");
        Thread.sleep(10); // 시간 차이를 위해
        ChatMessage message2 = ChatMessage.talk("room-1", "user1", "두 번째");

        assertThat(message1.getTimestamp()).isBefore(message2.getTimestamp());
    }
}