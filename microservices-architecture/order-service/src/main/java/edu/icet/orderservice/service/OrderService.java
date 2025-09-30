package edu.icet.orderservice.service;

import edu.icet.orderservice.dto.OrderDto;
import edu.icet.orderservice.dto.Response;

public interface OrderService {
    Response createOrder(Long userId);
    Response getAllOrders();
    Response getOrderById(Long orderId);
    Response getUserOrderHistory(Long userId);
}
