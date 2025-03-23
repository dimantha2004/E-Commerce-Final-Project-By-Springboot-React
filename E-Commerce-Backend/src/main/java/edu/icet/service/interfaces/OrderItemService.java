package edu.icet.service.interfaces;

import edu.icet.dto.OrderRequest;
import edu.icet.dto.Response;
import edu.icet.entity.Order;
import edu.icet.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderItemService {

    static void save(Order order) {
    }

    Response placeOrder(OrderRequest orderRequest);
    Response updateOrderItemStatus(Long orderItemId, String status);
    Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);

}
