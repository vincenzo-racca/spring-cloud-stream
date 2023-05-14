package com.vincenzoracca.springcloudstream.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/* For Spring Cloud Stream Reactive, DLQ and retry props doesn't work.
   You need to do custom management
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DlqEventUtil {

    private final StreamBridge streamBridge;

    public  <T> Mono<T> handleDLQ(T message, Throwable throwable, String channel) {
        log.error("Catch error in DLQ", throwable);
        streamBridge.send(channel, message);
        return Mono.empty();
    }
}
