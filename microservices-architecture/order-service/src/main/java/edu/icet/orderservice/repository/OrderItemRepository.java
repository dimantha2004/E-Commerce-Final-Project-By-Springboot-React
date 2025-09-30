package edu.icet.orderservice.repository;

import edu.icet.orderservice.entity.OrderItem;
import edu.icet.orderservice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByUserId(Long userId);
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByStatus(OrderStatus status);
    List<OrderItem> findByUserIdAndStatus(Long userId, OrderStatus status);
}
