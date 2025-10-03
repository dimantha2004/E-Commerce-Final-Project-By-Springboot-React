package edu.icet.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String userEmail;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus; // PENDING, COMPLETED, FAILED
    private String transactionId;
    private LocalDateTime paymentDate;
    private String eventType; // PAYMENT_INITIATED, PAYMENT_COMPLETED, PAYMENT_FAILED
}
