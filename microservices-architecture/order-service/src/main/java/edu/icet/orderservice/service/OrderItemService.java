package edu.icet.orderservice.service;

import edu.icet.orderservice.dto.OrderItemDto;
import edu.icet.orderservice.dto.Response;

public interface OrderItemService {
    Response placeOrder(OrderItemDto orderItemDto);
    Response updateOrderItemStatus(Long orderItemId, String status);
    Response filterOrderItems(String status);
    Response getAllOrderItems();
}
