package com.vincenzoracca.springcloudstream.dao;

import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SensorEventDao {

    Flux<SensorEventMessage> findAll();
    Mono<SensorEventMessage> save(Mono<SensorEventMessage> sensorEventMessageMono);
}
