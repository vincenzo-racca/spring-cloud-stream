# Spring Cloud Stream

## What is Spring Cloud Stream

Spring Cloud Stream is a Spring module based of another module, Spring Cloud Function. \
It allows creating event-driven applications by abstracting the binder type.

You can switch Kafka, RabbitMQ, AWS Kinesis and others, but you will not need to change anything in the code.

## About the project

This project is related to the tutorial: [Broker agnostic with Spring Cloud Stream](https://www.vincenzoracca.com/en/blog/framework/spring/cloud-stream/). \
This project shows how to use Spring Cloud Stream with Apache Kafka. Also:
- Shows how to advanced configure error handling with retry and DLQ.
- Shows how to use Spring Cloud Stream also with WebFlux (reactive mode).
- Shows how to switch the broker used by Kafka to RabbitMQ without changing the source code.

## Prerequisites
- Docker or Podman active
- Java 21
- 
## Run the project

There is a `docker-compose.yml` file in the root of project to run Zookeeper and Kafka containers. \
With Spring Boot 3.1, we have the docker compose support out of box (adding the **spring-boot-docker-compose** dependency).
So, you can just run the application with your IDE or with command: 
```shell
./mvnw clean spring-boot:run
```
and Spring will execute the docker compose in docker-compose.yml file and then the application.

There is an automatic producer that send the date every n seconds (the sensorEventAnotherProducer producer).


## Profiles in the project

The project works both imperative and reactive mode, by the spring profiles (**reactive** and **imperative**). \
The default mode is imperative, but you can replace the profile in _application.yml_ or with the command:
```shell
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=reactive
```

## DLQ and Retries

For the imperative mode, there is an automatic DQL and retry management of DLQ is provided by Spring. \
The DLQ is handled by the `enable-dlq` and `dlq-name` properties. When the consumer throws an exception, the message
will be processed again for two more times (the default of `maxAttempts` is 3), and after the message will be sent in DLQ.

For the reactive mode, you need to do a custom handling for DLQ and retries (see `DlqEventUtil` class).


## References
- https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream-binder-kafka.html#_apache_kafka_binder
- https://docs.spring.io/spring-cloud-stream/docs/3.1.3/reference/html/spring-cloud-stream.html#_whats_new_in_3_x
