package edu.icet.orderservice.dto;

import edu.icet.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private int quantity;
    private BigDecimal price;
    private OrderStatus status;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long orderId;
    private LocalDateTime createdAt;
}
