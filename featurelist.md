# 채팅 서버 구현 목록

## 1. 도메인 계층

### 1.1. ChatRoom (Entity)
- [x] roomId, roomName, participants, status, createdAt 필드
- [x] addUser(username) - 사용자 입장
- [x] removeUser(username) - 사용자 퇴장 (0명이면 자동 종료)
- [x] getUserCount() - 참가자 수 조회
- [x] isEmpty() - 방이 비었는지 확인
- [x] validateActive() - 활성 상태 검증
- [x] isClosed() - 종료 여부 확인

### 1.2. Participants (Value Object)
- [x] Set<String> usernames 필드
- [x] add(username) - 사용자 추가 (유효성 검증)
- [x] remove(username) - 사용자 제거
- [x] isEmpty() - 비어있는지 확인
- [x] getCount() - 사용자 수 조회
- [x] getUsernames() - 사용자 목록 조회 (방어적 복사)

### 1.3. ChatMessage (Entity)
- [x] type, roomId, sender, content, timestamp 필드
- [x] enter(roomId, sender) - 입장 메시지 생성
- [x] talk(roomId, sender, content) - 대화 메시지 생성
- [x] leave(roomId, sender) - 퇴장 메시지 생성

### 1.4. Enum
- [x] RoomStatus (ACTIVE, CLOSED)
- [x] MessageType (ENTER, TALK, LEAVE)

---

## 2. 서비스 계층

### 2.1. ChatRoomService
- [ ] Map<String, ChatRoom> rooms - 방 목록 관리
- [ ] createRoom(name) - 방 생성
- [ ] findAllRooms() - 전체 방 목록 조회 (ACTIVE만)
- [ ] findRoomById(roomId) - 특정 방 조회
- [ ] deleteRoom(roomId) - 방 삭제 (종료된 방 제거)
- [ ] addUserToRoom(roomId, username) - 사용자 입장
- [ ] removeUserFromRoom(roomId, username) - 사용자 퇴장
- [ ] handleDisconnect(roomId, username) - 연결 끊김 처리

---

## 3. 컨트롤러 계층

### 3.1. ChatRoomController (REST API)
- [ ] `@GetMapping("/api/rooms")` - 방 목록 조회
- [ ] `@PostMapping("/api/rooms")` - 방 생성
- [ ] `@GetMapping("/api/rooms/{roomId}")` - 특정 방 조회

### 3.2. ChatWebSocketController
- [ ] `@MessageMapping("/chat.sendMessage")` - 메시지 전송
    - [ ] ENTER 타입 처리 (입장)
    - [ ] TALK 타입 처리 (대화)
    - [ ] LEAVE 타입 처리 (퇴장)
- [ ] 브로드캐스트 (`/topic/chat/room/{roomId}`)

### 3.3. LobbyWebSocketController (새로 생성)
- [ ] `@MessageMapping("/lobby.subscribe")` - 로비 구독
- [ ] 방 생성 시 브로드캐스트 (`/topic/lobby`)
- [ ] 방 삭제 시 브로드캐스트 (`/topic/lobby`)

---

## 4. DTO 계층

### 4.1. 요청 DTO (dto/request)
- [ ] CreateRoomRequest - 방 생성 (name)
- [ ] SendMessageRequest - 메시지 전송 (type, roomId, sender, message)

### 4.2. 응답 DTO (dto/response)
- [ ] RoomInfoResponse - 방 정보 (roomId, roomName, userCount, status)
- [ ] MessageResponse - 메시지 응답 (type, roomId, sender, content, timestamp)
- [ ] LobbyUpdateResponse - 로비 업데이트 (type, room or roomId)
    - [ ] type: CREATED, DELETED, UPDATED

---

## 5. 설정 계층

### 5.1. WebSocketConfig
- [ ] STOMP 엔드포인트 등록 (`/ws-chat`)
- [ ] 메시지 브로커 설정 (`/topic`, `/app`)
- [ ] ChannelInterceptor 추가
    - [ ] SUBSCRIBE 시 roomId/username 세션에 저장

### 5.2. WebSocketEventListener (새로 생성)
- [ ] handleWebSocketDisconnectListener() - 연결 끊김 감지
    - [ ] 세션에서 roomId/username 추출
    - [ ] chatRoomService.handleDisconnect() 호출
    - [ ] LEAVE 메시지 브로드캐스트
    - [ ] 방이 비었으면 삭제 → 로비에 DELETED 브로드캐스트

### 5.3. WebConfig
- [ ] REST API CORS 설정

---

## 6. 예외 처리

### 6.1. 커스텀 예외
- [ ] RoomNotFoundException - 방을 찾을 수 없음
- [ ] RoomClosedException - 종료된 방
- [ ] InvalidUsernameException - 유효하지 않은 사용자명

### 6.2. GlobalExceptionHandler
- [ ] @ExceptionHandler로 예외 처리
- [ ] 에러 메시지 브로드캐스트