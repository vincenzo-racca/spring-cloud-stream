package com.vincenzoracca.springcloudstream.model;

import java.time.Instant;

public record SensorEventMessage(
        String sensorId,
        Instant timestampEvent,
        Double degree
)
{}
