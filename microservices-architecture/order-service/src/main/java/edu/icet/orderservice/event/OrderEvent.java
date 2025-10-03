package edu.icet.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private String userName;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderDate;
    private List<OrderItemEvent> orderItems;
    private String shippingAddress;
    private String eventType; // ORDER_CREATED, ORDER_UPDATED, ORDER_CANCELLED

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemEvent {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}
