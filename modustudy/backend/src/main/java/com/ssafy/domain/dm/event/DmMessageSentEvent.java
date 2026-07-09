package com.ssafy.domain.dm.event;

import com.ssafy.domain.dm.websocket.DmWebSocketEvent;

/**
 * DM 저장 완료 이벤트.
 * <p>
 * DM이 DB에 저장(커밋)된 뒤 수신자에게 실시간 푸시하기 위해 발행한다.
 * 실제 Redis pub/sub 발행은 {@code @TransactionalEventListener(AFTER_COMMIT)}에서 수행하여,
 * "커밋 전 발행 → 롤백 시 저장되지 않은 메시지가 푸시되는" 문제를 방지한다.
 *
 * @param receiverId 수신자 사용자 ID
 * @param payload    수신자에게 전달할 WebSocket 이벤트
 */
public record DmMessageSentEvent(Long receiverId, DmWebSocketEvent payload) {
}
