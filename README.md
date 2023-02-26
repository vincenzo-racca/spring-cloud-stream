# Spring Cloud Stream

## What is Spring Cloud Stream

Spring Cloud Stream is a Spring module based of another module, Spring Cloud Function. \
It allows creating event-driven applications by abstracting the binder type.

You can switch Kafka, RabbitMQ, AWS Kinesis and others, but you will not need to change anything in the code.

## About the project

In this project I show you the Spring Cloud Stream with Kafka. \
In the project there is an example of consumer and two examples of produces (both in imperative way and reactive way).


## Run the project

You can run a local kafka cluster by the docker-compose.yml in the project. \
From the root of project, run: `docker compose up -d`.
Then, you can run the application with your IDE or with command: `./mvnw spring-boot:run`

There is an automatic producer that send the date every 5 seconds (the sensorEventAnotherProducer producer).

## References
- https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream-binder-kafka.html#_apache_kafka_binder
- https://docs.spring.io/spring-cloud-stream/docs/3.1.3/reference/html/spring-cloud-stream.html#_whats_new_in_3_x
