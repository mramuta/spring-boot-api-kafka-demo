package com.mramuta.api_kafka_demo;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerApplication {
    private volatile boolean keepConsuming = true;
    private final Consumer<String, String> consumer;

    public KafkaConsumerApplication(final Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public void runConsume(final Properties consumerProps) {
        try {
            consumer.subscribe(Collections.singletonList(consumerProps.getProperty("input.topic.name")));
            while (keepConsuming) {
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
                consumerRecords.forEach(record -> System.out.println("NewUser::" + record.value()));
            }
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        keepConsuming = false;
    }

    public static Properties loadProperties() throws IOException {
        final Properties props = new Properties();
        InputStream propertiesStream = KafkaConsumerApplication.class
                .getResourceAsStream("/application.properties");
        props.load(propertiesStream);
        propertiesStream.close();
        return props;
    }

    public static void main(String[] args) throws Exception {
        final Properties consumerAppProps = KafkaConsumerApplication.loadProperties();
        final Consumer<String, String> consumer = new KafkaConsumer<>(consumerAppProps);
        final KafkaConsumerApplication consumerApplication = new KafkaConsumerApplication(consumer);

        Runtime.getRuntime().addShutdownHook(new Thread(consumerApplication::shutdown));

        consumerApplication.runConsume(consumerAppProps);
    }
}