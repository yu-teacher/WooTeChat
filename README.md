# WooteChat

온라인 바둑 게임을 위한 실시간 채팅 서버

## 기술 스택

- Java 21
- Spring Boot 3.5.8
- Spring WebSocket (STOMP)
- Lombok

## 주요 기능

- 실시간 채팅방 생성/관리
- WebSocket 기반 실시간 메시징
- 사용자 입장/퇴장 처리
- 로비 시스템 (방 목록 실시간 업데이트)

## 아키텍처

### 도메인 설계
- **ChatRoom**: 채팅방 엔티티 (Participants, RoomStatus 관리)
- **ChatMessage**: 메시지 엔티티 (ENTER, TALK, LEAVE)
- **Participants**: 참가자 관리 VO

### API 엔드포인트

**REST API**
- `GET /api/rooms` - 방 목록 조회
- `POST /api/rooms` - 방 생성
- `GET /api/rooms/{roomId}` - 특정 방 조회

**WebSocket**
- `/ws-chat` - WebSocket 연결
- `/app/chat.sendMessage` - 메시지 전송
- `/app/lobby.subscribe` - 로비 구독
- `/topic/chat/room/{roomId}` - 채팅방 구독
- `/topic/lobby` - 로비 업데이트 구독

## 실행 방법

### 로컬 실행
```bash
./gradlew bootRun
```

### Docker 실행
```bash
./gradlew bootJar
docker build -t wootechat .
docker run -p 9002:9002 wootechat
```

서버는 `9002` 포트에서 실행됩니다.

## 프로젝트 구조

```
src/main/java/com/woowa/wootechat/
├── config/              # WebSocket, CORS 설정
├── controller/          # REST, WebSocket 컨트롤러
├── domain/              # 도메인 모델
│   ├── message/        # 메시지 관련
│   └── room/           # 채팅방 관련
├── dto/                # 요청/응답 DTO
├── exception/          # 예외 처리
└── service/            # 비즈니스 로직
```