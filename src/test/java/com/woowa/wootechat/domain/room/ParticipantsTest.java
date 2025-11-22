package com.woowa.wootechat.domain.room;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ParticipantsTest {

    @Test
    void 초기_상태_확인() {
        Participants participants = new Participants();

        assertThat(participants.isEmpty()).isTrue();
        assertThat(participants.getCount()).isZero();
        assertThat(participants.getUsernames()).isEmpty();
    }

    @Test
    void 사용자_추가() {
        Participants participants = new Participants();

        participants.add("user1");

        assertThat(participants.isEmpty()).isFalse();
        assertThat(participants.getCount()).isEqualTo(1);
        assertThat(participants.contains("user1")).isTrue();
    }

    @Test
    void 여러_사용자_추가() {
        Participants participants = new Participants();

        participants.add("user1");
        participants.add("user2");
        participants.add("user3");

        assertThat(participants.getCount()).isEqualTo(3);
        assertThat(participants.getUsernames()).containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    void 중복_사용자_추가시_무시됨() {
        Participants participants = new Participants();

        participants.add("user1");
        participants.add("user1");
        participants.add("user1");

        assertThat(participants.getCount()).isEqualTo(1);
    }

    @Test
    void null_사용자_추가시_예외() {
        Participants participants = new Participants();

        assertThatThrownBy(() -> participants.add(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 사용자명입니다");
    }

    @Test
    void 빈_문자열_사용자_추가시_예외() {
        Participants participants = new Participants();

        assertThatThrownBy(() -> participants.add(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 사용자명입니다");
    }

    @Test
    void 공백_사용자_추가시_예외() {
        Participants participants = new Participants();

        assertThatThrownBy(() -> participants.add("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 사용자명입니다");
    }

    @Test
    void 사용자_제거() {
        Participants participants = new Participants();
        participants.add("user1");
        participants.add("user2");

        participants.remove("user1");

        assertThat(participants.getCount()).isEqualTo(1);
        assertThat(participants.contains("user1")).isFalse();
        assertThat(participants.contains("user2")).isTrue();
    }

    @Test
    void 모든_사용자_제거() {
        Participants participants = new Participants();
        participants.add("user1");
        participants.add("user2");

        participants.remove("user1");
        participants.remove("user2");

        assertThat(participants.isEmpty()).isTrue();
        assertThat(participants.getCount()).isZero();
    }

    @Test
    void 존재하지_않는_사용자_제거시_예외_없음() {
        Participants participants = new Participants();
        participants.add("user1");

        assertThatCode(() -> participants.remove("user2"))
                .doesNotThrowAnyException();

        assertThat(participants.getCount()).isEqualTo(1);
    }

    @Test
    void getUsernames는_방어적_복사_반환() {
        Participants participants = new Participants();
        participants.add("user1");

        Set<String> usernames = participants.getUsernames();
        usernames.add("user2"); // 외부에서 수정 시도

        // 원본은 영향받지 않음
        assertThat(participants.getCount()).isEqualTo(1);
        assertThat(participants.contains("user2")).isFalse();
    }

    @Test
    void contains_확인() {
        Participants participants = new Participants();
        participants.add("user1");

        assertThat(participants.contains("user1")).isTrue();
        assertThat(participants.contains("user2")).isFalse();
    }
}
