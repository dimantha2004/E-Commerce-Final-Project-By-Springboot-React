package edu.icet.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String message;
    private String notificationType;
    private String templateName;
    private Object templateData;
    private LocalDateTime createdAt;
    private String eventType;
}
