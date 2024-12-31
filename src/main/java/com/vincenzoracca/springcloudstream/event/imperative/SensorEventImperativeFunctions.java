package com.vincenzoracca.springcloudstream.event.imperative;

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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

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
        final RandomGenerator random = RandomGenerator.getDefault();
        return () -> new SensorEventMessage("2", Instant.now(), random.nextDouble(1.0, 31.0));
    }


    @Bean
    public Function<SensorEventMessage, SensorEventMessage> logEventReceived() {
        return sensorEventMessage ->  {
            log.info("Message received: {}", sensorEventMessage);
            return sensorEventMessage;
        };
    }

    @Bean
    public Consumer<SensorEventMessage> saveInDBEventReceived() {
        return sensorEventMessage -> sensorEventDao.save(Mono.just(sensorEventMessage)).block();
    }
}
