package com.woowa.wootechat.domain.room;

import java.util.HashSet;
import java.util.Set;

/**
 * 참가자 관리 Value Object
 */
public class Participants {

    private final Set<String> usernames;

    public Participants() {
        this.usernames = new HashSet<>();
    }

    /**
     * 사용자 추가
     */
    public void add(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 사용자명입니다.");
        }
        usernames.add(username);
    }

    /**
     * 사용자 제거
     */
    public void remove(String username) {
        usernames.remove(username);
    }

    /**
     * 비어있는지 확인
     */
    public boolean isEmpty() {
        return usernames.isEmpty();
    }

    /**
     * 사용자 수 조회
     */
    public int getCount() {
        return usernames.size();
    }

    /**
     * 사용자 목록 조회 (방어적 복사)
     */
    public Set<String> getUsernames() {
        return new HashSet<>(usernames);
    }

    /**
     * 특정 사용자 포함 여부
     */
    public boolean contains(String username) {
        return usernames.contains(username);
    }
}
