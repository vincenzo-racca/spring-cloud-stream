package com.vincenzoracca.springcloudstream.event;

import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Imperative version of @{@link SensorEventImperativeFunctions}.
 * Configuration and Bean are commented because I want to use the reactive version
 */
//@Configuration
@Slf4j
public class SensorEventImperativeFunctions {

//    @Bean
    public Supplier<SensorEventMessage> sensorEventProducer() {
        return () -> new SensorEventMessage("2", Instant.now(), 30.0);
    }

//    @Bean
    public Consumer<SensorEventMessage> logEventReceived() {
        return sensorEventMessage -> log.info("Message received: {}", sensorEventMessage);
    }
}
