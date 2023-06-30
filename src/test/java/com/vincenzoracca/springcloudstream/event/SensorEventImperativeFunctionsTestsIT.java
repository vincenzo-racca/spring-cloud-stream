package com.vincenzoracca.springcloudstream.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincenzoracca.springcloudstream.dao.SensorEventDao;
import com.vincenzoracca.springcloudstream.model.SensorEventMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("imperative")
@Import(TestChannelBinderConfiguration.class)
class SensorEventImperativeFunctionsTestsIT {


    @Autowired
    private InputDestination input;

    @Autowired
    private OutputDestination output;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProducerSensorEvent producerSensorEvent;

    @Autowired
    private SensorEventDao sensorEventDao;


    @Test
    void produceSyncMessageTest() throws IOException {
        SensorEventMessage sensorEventMessage = new SensorEventMessage("3", Instant.now(), 15.0);
        producerSensorEvent.publishMessage(sensorEventMessage);
        byte[] payloadBytesReceived = output.receive(100L, "sensor_event_topic").getPayload();
        assertThat(objectMapper.readValue(payloadBytesReceived, SensorEventMessage.class)).isEqualTo(sensorEventMessage);
    }

    @Test
    void producePollableMessageTest() throws IOException {
        byte[] payloadBytesReceived = output.receive(5000L, "sensor_event_topic").getPayload();
        SensorEventMessage sensorEventMessageReceived = objectMapper.readValue(payloadBytesReceived, SensorEventMessage.class);
        assertThat(sensorEventMessageReceived.sensorId()).isEqualTo("2");
        assertThat(sensorEventMessageReceived.degrees()).isEqualTo(30.0);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void consumeMessageTest() throws IOException {
        SensorEventMessage sensorEventMessage = new SensorEventMessage("3", Instant.now(), 15.0);
        byte[] payloadBytesSend = objectMapper.writeValueAsBytes(sensorEventMessage);
        input.send(new GenericMessage<>(payloadBytesSend), "sensor_event_topic");
        StepVerifier
                .create(sensorEventDao.findAll())
                .expectNext(sensorEventMessage)
                .expectComplete()
                .verify();
    }

    //TODO test for DLQ

}
