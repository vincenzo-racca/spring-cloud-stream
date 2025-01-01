package com.vincenzoracca.springcloudstream.event.reactive;

import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.event.DlqEventUtil;
import com.vincenzoracca.springcloudstream.model.SensorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

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
    public Function<Flux<SensorEvent>, Mono<Void>> logEventReceivedSaveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                .flatMap(this::consumeMessage)
                .then();
    }

    private Mono<Void> consumeMessage(SensorEvent message) {
        return logEventReceived().andThen(saveInDBEventReceived()).apply(Flux.just(message))
                .retry(2)
                .onErrorResume(throwable -> dlqEventUtil.handleDLQ(message, throwable, DLQ_CHANNEL));
    }


//    @Bean
    public Function<Flux<SensorEvent>, Flux<SensorEvent>> logEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEvent -> log.info("Message received: {}", sensorEvent));
    }

    // for the reactive consumers, you can use Consumer<Flux<..>> or Function<Flux<..>, Mono<Void>>
//    @Bean
//    public Consumer<Flux<SensorEvent>> logEventReceived() {
//        return fluxEvent -> {
//            fluxEvent
//                    .doOnNext(sensorEvent -> log.info("Message received: {}", sensorEvent))
//                    .subscribe();
//
//        };
//    }

//    @Bean
    public Function<Flux<SensorEvent>, Mono<Void>> saveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                .flatMap(sensorEvent -> sensorEventDao.save(Mono.just(sensorEvent)))
                .then();
    }


    @PollableBean
    public Supplier<Flux<SensorEvent>> sensorEventProducer() {
        final RandomGenerator random = RandomGenerator.getDefault();
        return () -> Flux.just(new SensorEvent("2", Instant.now(), random.nextDouble(1.0, 31.0)));
    }

}
