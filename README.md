# Spring Cloud Stream

## What is Spring Cloud Stream

Spring Cloud Stream is a Spring module based of another module, Spring Cloud Function. \
It allows creating event-driven applications by abstracting the binder type.

You can switch Kafka, RabbitMQ, AWS Kinesis and others, but you will not need to change anything in the code.

## About the project

In this project I show you the Spring Cloud Stream with Kafka. \
In the project there is an example of consumer and two examples of produces (both in imperative way and reactive way). \
In the SensorEventFunctions class, I show you Spring Cloud Stream in reactive mode,
in the SensorEventImperativeFunctions, I show you the same flow in the imperative mode.


## Run the project

You can run a local kafka cluster by the docker-compose.yml in the project. \
From the root of project, run: `docker compose up -d`.
Then, you can run the application with your IDE or with command: `./mvnw spring-boot:run`

There is an automatic producer that send the date every 5 seconds (the sensorEventAnotherProducer producer).


## Profiles in the project

The project works both imperative and reactive mode, by the spring profiles (**reactive** and **imperative**). \
The default mode is reactive, but you can replace the profile in _application.yml_.

## DLQ and Retries

For the imperative mode, there is an automatic DQL and retry management of DLQ is provided by Spring. \
The DLQ is handled by the `enable-dlq` and `dlq-name` properties. When the consumer throws an exception, the message
will be processed again for two more times (the default of `maxAttempts` is 3), and after the message will be sent in DLQ.

For the reactive mode, you need to do a custom handling for DLQ and retries (see `DlqEventUtil` class).


## References
- https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream-binder-kafka.html#_apache_kafka_binder
- https://docs.spring.io/spring-cloud-stream/docs/3.1.3/reference/html/spring-cloud-stream.html#_whats_new_in_3_x
