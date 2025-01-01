package com.vincenzoracca.springcloudstream.dao;

import com.vincenzoracca.springcloudstream.model.SensorEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SensorEventDao {

    Flux<SensorEvent> findAll();
    Mono<SensorEvent> save(Mono<SensorEvent> sensorEventMono);
}
