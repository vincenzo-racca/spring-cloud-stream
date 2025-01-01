package com.vincenzoracca.springcloudstream.dao;

import com.vincenzoracca.springcloudstream.model.SensorEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class SensorEventInMemoryDao implements SensorEventDao {

    private final ConcurrentHashMap<String, SensorEvent> memoryDB = new ConcurrentHashMap<>();


    public Flux<SensorEvent> findAll() {
        return Flux.fromIterable(memoryDB.values());
    }

    public Mono<SensorEvent> save(Mono<SensorEvent> sensorEventMono) {
        return sensorEventMono
                .doOnNext(sensorEvent -> log.info("Message saving: {}", sensorEvent))
                .flatMap(sensorEvent -> {
                    if (sensorEvent.degrees() == 10.0) return Mono.error(new RuntimeException("Error in saveMessage"));
                    return Mono.just(sensorEvent);
                })
                .doOnNext(sensorEvent -> memoryDB.put(sensorEvent.sensorId(), sensorEvent))
                .doOnNext(sensorEvent -> log.info("Message saved with id: {}", sensorEvent.sensorId()));
    }

}
