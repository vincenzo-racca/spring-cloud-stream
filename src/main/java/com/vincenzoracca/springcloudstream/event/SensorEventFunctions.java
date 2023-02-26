package com.vincenzoracca.springcloudstream.event;

import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SensorEventFunctions {

    private final SensorEventDao sensorEventDao;

    @Bean
    public Function<Flux<SensorEventMessage>, Flux<SensorEventMessage>> logEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEventMessage -> log.info("Message received: {}", sensorEventMessage));
    }

    @Bean
    public Function<Flux<SensorEventMessage>, Mono<Void>> saveInDBEventReceived() {
        return fluxEvent -> fluxEvent
                .doOnNext(sensorEventMessage -> log.info("Message saving: {}", sensorEventMessage))
                .flatMap(sensorEventMessage -> sensorEventDao.save(Mono.just(sensorEventMessage)))
                .then();
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

//    @PollableBean
    @Bean
    public Supplier<Flux<SensorEventMessage>> sensorEventAnotherProducer() {
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


// IMPERATIVE MODE

//    @Bean
//    public Supplier<SensorEventMessage> sensorEventAnotherProducer() {
//        return () -> {
//            SensorEventMessage sensorEventMessage = new SensorEventMessage("2", Instant.now(), 30.0);
//            return sensorEventMessage;
//        };
//    }

//    @Bean
//    public Consumer<SensorEventMessage> sensorEventMessageConsumer() {
//        return sensorEventMessage -> {
//            log.info("Message received: {}", sensorEventMessage);
//        };
//    }

}
