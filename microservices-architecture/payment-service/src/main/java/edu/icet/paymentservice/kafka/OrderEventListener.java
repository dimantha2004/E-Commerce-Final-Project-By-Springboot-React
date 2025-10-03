package edu.icet.paymentservice.kafka;

import edu.icet.paymentservice.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final PaymentEventPublisher paymentEventPublisher;

    @KafkaListener(topics = KafkaTopics.PAYMENT_INITIATED, groupId = "payment-service-group")
    public void handlePaymentInitiated(PaymentEvent paymentEvent) {
        log.info("Received payment initiated event for order: {}", paymentEvent.getOrderId());

        try {
            boolean paymentSuccessful = processPayment(paymentEvent);

            if (paymentSuccessful) {
                paymentEvent.setPaymentStatus("COMPLETED");
                paymentEvent.setTransactionId("TXN-" + System.currentTimeMillis());
                paymentEventPublisher.publishPaymentCompleted(paymentEvent);
            } else {
                paymentEvent.setPaymentStatus("FAILED");
                paymentEventPublisher.publishPaymentFailed(paymentEvent);
            }

        } catch (Exception e) {
            log.error("Error processing payment for order: {}", paymentEvent.getOrderId(), e);
            paymentEvent.setPaymentStatus("FAILED");
            paymentEventPublisher.publishPaymentFailed(paymentEvent);
        }
    }

    private boolean processPayment(PaymentEvent paymentEvent) {
        // Simulate payment processing
        log.info("Processing payment for order: {} with amount: {}",
                paymentEvent.getOrderId(), paymentEvent.getAmount());

        // Simulate 90% success rate
        return Math.random() > 0.1;
    }
}
