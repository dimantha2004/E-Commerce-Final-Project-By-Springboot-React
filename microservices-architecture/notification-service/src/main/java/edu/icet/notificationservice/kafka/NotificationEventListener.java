package edu.icet.notificationservice.kafka;

import edu.icet.notificationservice.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = KafkaTopics.NOTIFICATION_SEND, groupId = "notification-service-group")
    public void handleNotificationSend(NotificationEvent notificationEvent) {
        log.info("Received notification event for: {}", notificationEvent.getRecipientEmail());

        try {
            sendEmailNotification(notificationEvent);
            log.info("Email notification sent successfully to: {}", notificationEvent.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email notification to: {}", notificationEvent.getRecipientEmail(), e);
        }
    }

    private void sendEmailNotification(NotificationEvent notificationEvent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationEvent.getRecipientEmail());
        message.setSubject(notificationEvent.getSubject());
        message.setText(notificationEvent.getMessage());
        message.setFrom("noreply@ecommerce.com");

        mailSender.send(message);
    }
}
