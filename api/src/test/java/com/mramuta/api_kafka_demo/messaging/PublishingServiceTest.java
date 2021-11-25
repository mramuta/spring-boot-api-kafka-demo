package com.mramuta.api_kafka_demo.messaging;

import com.mramuta.api_kafka_demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class PublishingServiceTest {

    private PublishingService subject;
    @Mock
    private KafkaTemplate<Long, User> kafkaTemplate;

    @BeforeEach
    void setUp() {
        openMocks(this);
        subject = new PublishingService(kafkaTemplate);
    }

    @Test
    void shouldPublishMessage() {
        User expectedUser = new User(
                99L,
                "someFirst",
                "someLast",
                "someEmail",
                "someHash"
        );
        subject.publishNewUserEvent(expectedUser);
        verify(kafkaTemplate).send(
                "user",
                expectedUser.getId(),
                expectedUser);
    }
}