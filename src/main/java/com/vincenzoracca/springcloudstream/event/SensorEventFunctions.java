package com.vincenzoracca.springcloudstream.event;

import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SensorEventFunctions {

    private static final String DLQ_CHANNEL = "sensorEventDlqProducer-out-0";

    private final SensorEventDao sensorEventDao;

    private final DlqEventUtil dlqEventUtil;

    @Bean
    public Function<Flux<SensorEventMessage>, Flux<SensorEventMessage>> logEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEventMessage -> log.info("Message received: {}", sensorEventMessage));
    }

    @Bean
    public Function<Flux<SensorEventMessage>, Mono<Void>> saveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                // to manage DLQ, you can wrap the flow in a flatMap
                .flatMap(this::consumeMessage)
//                .doOnNext(sensorEventMessage -> log.info("Message saving: {}", sensorEventMessage))
//                .flatMap(sensorEventMessage -> sensorEventDao.save(Mono.just(sensorEventMessage)))
                .then();
    }

    @NotNull
    private Mono<SensorEventMessage> consumeMessage(SensorEventMessage sensorEventMessage) {
        log.info("Message saving: {}", sensorEventMessage);
        return sensorEventDao.save(Mono.just(sensorEventMessage))
                .map(sensorEventMessage1 -> {
                    if (sensorEventMessage.degrees() == 10.0) throw new RuntimeException("Error in saveMessage");
                    return sensorEventMessage1;
                })
                .onErrorResume(throwable -> dlqEventUtil.handleDLQ(sensorEventMessage, throwable, DLQ_CHANNEL));
    }

    //    @PollableBean
    @Bean
    public Supplier<Flux<SensorEventMessage>> sensorEventProducer() {
        return () -> Flux.fromStream(Stream.generate(() -> {
            try {
                Thread.sleep(5000);
                return new SensorEventMessage("2", Instant.now(), 30.0);
            } catch (Exception e) {
                // ignore
            }
            return null;
        })).subscribeOn(Schedulers.boundedElastic()).share();
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
