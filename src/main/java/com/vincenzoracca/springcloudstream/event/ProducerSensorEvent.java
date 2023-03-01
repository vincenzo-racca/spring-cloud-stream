package com.vincenzoracca.springcloudstream.event;

import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerSensorEvent {

    private final StreamBridge streamBridge;


    public boolean publishMessage(SensorEventMessage message) {
        return streamBridge.send("sensorEventAnotherProducer-out-0", message);
    }

}
