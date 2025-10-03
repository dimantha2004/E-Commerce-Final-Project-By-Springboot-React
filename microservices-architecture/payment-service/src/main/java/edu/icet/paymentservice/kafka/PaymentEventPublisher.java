package edu.icet.paymentservice.kafka;

import edu.icet.paymentservice.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentEvent paymentEvent) {
        log.info("Publishing payment completed event for order: {}", paymentEvent.getOrderId());
        paymentEvent.setEventType("PAYMENT_COMPLETED");
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED, paymentEvent.getOrderId().toString(), paymentEvent);
    }

    public void publishPaymentFailed(PaymentEvent paymentEvent) {
        log.info("Publishing payment failed event for order: {}", paymentEvent.getOrderId());
        paymentEvent.setEventType("PAYMENT_FAILED");
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, paymentEvent.getOrderId().toString(), paymentEvent);
    }
}
