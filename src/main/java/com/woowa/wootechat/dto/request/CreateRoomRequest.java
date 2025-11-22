package com.woowa.wootechat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 방 생성 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    private String name;
}