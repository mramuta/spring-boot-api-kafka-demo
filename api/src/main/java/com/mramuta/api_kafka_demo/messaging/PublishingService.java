package com.mramuta.api_kafka_demo.messaging;

import com.mramuta.api_kafka_demo.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublishingService {

    private final KafkaTemplate<Long, User> userKafkaTemplate;

    public PublishingService(KafkaTemplate<Long, User> userKafkaTemplate) {
        this.userKafkaTemplate = userKafkaTemplate;
    }

    public void publishNewUserEvent(User user) {
        userKafkaTemplate.send("user", user.getId(), user);
    }
}
