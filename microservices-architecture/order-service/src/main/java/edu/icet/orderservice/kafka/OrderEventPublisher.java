package edu.icet.orderservice.kafka;

import edu.icet.orderservice.event.NotificationEvent;
import edu.icet.orderservice.event.OrderEvent;
import edu.icet.orderservice.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderEvent orderEvent) {
        log.info("Publishing order created event for order: {}", orderEvent.getOrderId());
        orderEvent.setEventType("ORDER_CREATED");
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, orderEvent.getOrderId().toString(), orderEvent);

        // Also trigger payment initiation
        PaymentEvent paymentEvent = createPaymentEvent(orderEvent);
        publishPaymentInitiated(paymentEvent);

        // Send order confirmation notification
        NotificationEvent notificationEvent = createOrderNotification(orderEvent);
        publishNotification(notificationEvent);
    }

    public void publishOrderUpdated(OrderEvent orderEvent) {
        log.info("Publishing order updated event for order: {}", orderEvent.getOrderId());
        orderEvent.setEventType("ORDER_UPDATED");
        kafkaTemplate.send(KafkaTopics.ORDER_UPDATED, orderEvent.getOrderId().toString(), orderEvent);
    }

    public void publishOrderCancelled(OrderEvent orderEvent) {
        log.info("Publishing order cancelled event for order: {}", orderEvent.getOrderId());
        orderEvent.setEventType("ORDER_CANCELLED");
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, orderEvent.getOrderId().toString(), orderEvent);

        // Send cancellation notification
        NotificationEvent notificationEvent = createCancellationNotification(orderEvent);
        publishNotification(notificationEvent);
    }

    public void publishPaymentInitiated(PaymentEvent paymentEvent) {
        log.info("Publishing payment initiated event for order: {}", paymentEvent.getOrderId());
        paymentEvent.setEventType("PAYMENT_INITIATED");
        kafkaTemplate.send(KafkaTopics.PAYMENT_INITIATED, paymentEvent.getOrderId().toString(), paymentEvent);
    }

    public void publishNotification(NotificationEvent notificationEvent) {
        log.info("Publishing notification event for: {}", notificationEvent.getRecipientEmail());
        notificationEvent.setEventType("SEND_NOTIFICATION");
        kafkaTemplate.send(KafkaTopics.NOTIFICATION_SEND, notificationEvent.getRecipientEmail(), notificationEvent);
    }

    private PaymentEvent createPaymentEvent(OrderEvent orderEvent) {
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setOrderId(orderEvent.getOrderId());
        paymentEvent.setUserId(orderEvent.getUserId());
        paymentEvent.setUserEmail(orderEvent.getUserEmail());
        paymentEvent.setAmount(orderEvent.getTotalAmount());
        paymentEvent.setPaymentStatus("PENDING");
        paymentEvent.setPaymentDate(orderEvent.getOrderDate());
        return paymentEvent;
    }

    private NotificationEvent createOrderNotification(OrderEvent orderEvent) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setRecipientEmail(orderEvent.getUserEmail());
        notificationEvent.setRecipientName(orderEvent.getUserName());
        notificationEvent.setSubject("Order Confirmation - Order #" + orderEvent.getOrderId());
        notificationEvent.setMessage("Your order has been placed successfully. Order ID: " + orderEvent.getOrderId());
        notificationEvent.setNotificationType("EMAIL");
        notificationEvent.setTemplateName("order-confirmation");
        notificationEvent.setTemplateData(orderEvent);
        return notificationEvent;
    }

    private NotificationEvent createCancellationNotification(OrderEvent orderEvent) {
        NotificationEvent notificationEvent = new NotificationEvent();
        notificationEvent.setRecipientEmail(orderEvent.getUserEmail());
        notificationEvent.setRecipientName(orderEvent.getUserName());
        notificationEvent.setSubject("Order Cancelled - Order #" + orderEvent.getOrderId());
        notificationEvent.setMessage("Your order has been cancelled. Order ID: " + orderEvent.getOrderId());
        notificationEvent.setNotificationType("EMAIL");
        notificationEvent.setTemplateName("order-cancellation");
        notificationEvent.setTemplateData(orderEvent);
        return notificationEvent;
    }
}
