package edu.icet.orderservice.kafka;

import edu.icet.orderservice.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventListener {

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED, groupId = "order-service-group")
    public void handlePaymentCompleted(PaymentEvent paymentEvent) {
        log.info("Received payment completed event for order: {}", paymentEvent.getOrderId());

        log.info("Order {} status updated to PAID", paymentEvent.getOrderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "order-service-group")
    public void handlePaymentFailed(PaymentEvent paymentEvent) {
        log.info("Received payment failed event for order: {}", paymentEvent.getOrderId());

        log.info("Order {} status updated to PAYMENT_FAILED", paymentEvent.getOrderId());
    }
}
