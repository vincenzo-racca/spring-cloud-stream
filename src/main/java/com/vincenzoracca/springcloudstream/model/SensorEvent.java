package com.vincenzoracca.springcloudstream.model;

import java.time.Instant;

public record SensorEvent(
        String sensorId,
        Instant timestampEvent,
        Double degrees
)
{}
