package com.vincenzoracca.springcloudstream.event;

import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Imperative version of @{@link SensorEventImperativeFunctions}.
 * Configuration and Bean are commented because I want to use the reactive version
 */
@Configuration
@Profile("imperative")
@Slf4j
@RequiredArgsConstructor
public class SensorEventImperativeFunctions {

    private final SensorEventDao sensorEventDao;

    @Bean
    public Supplier<SensorEventMessage> sensorEventProducer() {
        return () -> new SensorEventMessage("2", Instant.now(), 30.0);
    }


    @Bean
    public Consumer<SensorEventMessage> logEventReceivedSaveInDBEventReceived() {
        return sensorEventMessage ->  {
            log.info("Message received: {}", sensorEventMessage);
            sensorEventDao.save(Mono.just(sensorEventMessage)).block();
        };
    }
}
