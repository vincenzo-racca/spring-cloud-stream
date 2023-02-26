package com.vincenzoracca.springcloudstream.dao;

import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class SensorEventInMemoryDao implements SensorEventDao {

    private final ConcurrentHashMap<String, SensorEventMessage> memoryDB = new ConcurrentHashMap<>();


    public Flux<SensorEventMessage> findAll() {
        return Flux.fromIterable(memoryDB.values());
    }

    public Mono<Void> save(Mono<SensorEventMessage> sensorEventMessageMono) {
        return sensorEventMessageMono
                .doOnNext(sensorEventMessage -> memoryDB.put(sensorEventMessage.sensorId(), sensorEventMessage))
                .doOnNext(sensorEventMessage -> log.info("Message saved with id: {}", sensorEventMessage.sensorId()))
                .then();
    }

}
