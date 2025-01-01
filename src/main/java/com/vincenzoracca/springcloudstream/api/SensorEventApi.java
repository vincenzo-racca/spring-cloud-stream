package com.vincenzoracca.springcloudstream.api;

import com.vincenzoracca.springcloudstream.event.ProducerSensorEvent;
import com.vincenzoracca.springcloudstream.model.SensorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sensor-event")
@RequiredArgsConstructor
public class SensorEventApi {

    private final ProducerSensorEvent producerSensorEvent;


    @PostMapping
    public Mono<ResponseEntity<Boolean>> sendDate(@RequestBody Mono<SensorEvent> sensorEvent) {
        return sensorEvent
                .map(producerSensorEvent::publishMessage)
                .map(ResponseEntity::ok);

    }

}
