package com.vincenzoracca.springcloudstream.reactive;

import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.event.DlqEventUtil;
import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@Profile("reactive")
@Slf4j
@RequiredArgsConstructor
public class SensorEventFunctions {

    private static final String DLQ_CHANNEL = "sensorEventDlqProducer-out-0";

    private final SensorEventDao sensorEventDao;

    private final DlqEventUtil dlqEventUtil;

    // to manage DLQ and retry, you can wrap the flow in a flatMap
    @Bean
    public Function<Flux<Message<SensorEventMessage>>, Mono<Void>> logEventReceivedSaveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                .flatMap(this::consumeMessage)
                .then();
    }

    private Mono<SensorEventMessage> consumeMessage(Message<SensorEventMessage> message) {
        return Mono.just(message)
                .doOnNext(sensorEventMessage -> log.info("Message received: {}", sensorEventMessage))
                .flatMap(sensorEventMessage -> sensorEventDao.save(Mono.just(sensorEventMessage.getPayload())))
                .retry(2)
                .onErrorResume(throwable -> dlqEventUtil.handleDLQ(message, throwable, DLQ_CHANNEL));
    }


//    @Bean
    public Function<Flux<SensorEventMessage>, Flux<SensorEventMessage>> logEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEventMessage -> log.info("Message received: {}", sensorEventMessage));
    }

//    @Bean
    public Function<Flux<SensorEventMessage>, Mono<Void>> saveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEventMessage -> log.info("Message saving: {}", sensorEventMessage))
                .flatMap(sensorEventMessage -> sensorEventDao.save(Mono.just(sensorEventMessage)))
                .then();
    }



    @PollableBean
    public Supplier<Mono<SensorEventMessage>> sensorEventProducer() {
        return () -> Mono.just(new SensorEventMessage("2", Instant.now(), 30.0));
    }

    // for the reactive consumers, you can use Consumer<Flux<..>> or Function<Flux<..>, Mono<Void>>
//    @Bean
//    public Consumer<Flux<SensorEventMessage>> logEventReceived() {
//        return fluxEvent -> {
//            fluxEvent
//                    .doOnNext(sensorEventMessage -> log.info("Message received: {}", sensorEventMessage))
//                    .subscribe();
//
//        };
//    }

}
