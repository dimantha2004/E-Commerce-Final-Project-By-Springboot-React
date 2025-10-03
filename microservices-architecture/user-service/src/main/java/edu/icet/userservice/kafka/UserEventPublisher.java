package edu.icet.userservice.kafka;

import edu.icet.userservice.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(UserEvent userEvent) {
        log.info("Publishing user created event for user: {}", userEvent.getUserId());
        userEvent.setEventType("USER_CREATED");
        kafkaTemplate.send(KafkaTopics.USER_CREATED, userEvent.getUserId().toString(), userEvent);
    }

    public void publishUserUpdated(UserEvent userEvent) {
        log.info("Publishing user updated event for user: {}", userEvent.getUserId());
        userEvent.setEventType("USER_UPDATED");
        kafkaTemplate.send(KafkaTopics.USER_UPDATED, userEvent.getUserId().toString(), userEvent);
    }
}
