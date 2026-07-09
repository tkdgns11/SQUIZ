package com.ssafy.domain.dm.websocket;

import com.ssafy.domain.dm.event.DmMessageSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * DM 실시간 푸시 리스너.
 * <p>
 * DM이 DB에 <b>커밋된 뒤(AFTER_COMMIT)</b>에만 Redis pub/sub으로 발행한다.
 * 커밋 전에 발행하면 이후 트랜잭션이 롤백될 때 저장되지 않은 메시지가 푸시될 수 있으므로,
 * 커밋 확정 이후로 발행 시점을 미룬다. (푸시 실패는 로깅만 하고 삼킨다 — 메시지는 이미 DB에 있어
 * 수신자가 대화를 열 때 이력 조회로 복구된다.)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DmMessagePushListener {

    private final DmRedisPublisher dmRedisPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSent(DmMessageSentEvent event) {
        try {
            dmRedisPublisher.publishToUser(event.receiverId(), "/queue/dm", event.payload());
        } catch (Exception e) {
            log.warn("DM 실시간 푸시 실패 (메시지는 저장됨): receiverId={}, error={}",
                    event.receiverId(), e.getMessage());
        }
    }
}
