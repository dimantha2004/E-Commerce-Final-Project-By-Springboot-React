package edu.icet.orderservice.service.impl;

import edu.icet.orderservice.dto.OrderDto;
import edu.icet.orderservice.dto.OrderItemDto;
import edu.icet.orderservice.dto.Response;
import edu.icet.orderservice.entity.Order;
import edu.icet.orderservice.entity.OrderItem;
import edu.icet.orderservice.enums.OrderStatus;
import edu.icet.orderservice.repository.OrderItemRepository;
import edu.icet.orderservice.repository.OrderRepository;
import edu.icet.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Response createOrder(Long userId) {
        try {
            // Get all pending order items for the user (cart items)
            List<OrderItem> cartItems = orderItemRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING);

            if (cartItems.isEmpty()) {
                return Response.builder()
                        .status(400)
                        .message("No items in cart")
                        .build();
            }

            // Calculate total price
            BigDecimal totalPrice = cartItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalPrice(totalPrice);

            Order savedOrder = orderRepository.save(order);

            // Update cart items to belong to this order and set status to CONFIRMED
            cartItems.forEach(item -> {
                item.setOrder(savedOrder);
                item.setStatus(OrderStatus.CONFIRMED);
            });
            orderItemRepository.saveAll(cartItems);

            OrderDto orderDto = mapToOrderDto(savedOrder);
            return Response.builder()
                    .status(200)
                    .message("Order created successfully")
                    .order(orderDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error creating order: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            List<OrderDto> orderDtos = orders.stream()
                    .map(this::mapToOrderDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .orderList(orderDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching orders: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getOrderById(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            OrderDto orderDto = mapToOrderDto(order);
            return Response.builder()
                    .status(200)
                    .order(orderDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(404)
                    .message("Order not found: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getUserOrderHistory(Long userId) {
        try {
            List<Order> orders = orderRepository.findByUserId(userId);
            List<OrderDto> orderDtos = orders.stream()
                    .map(this::mapToOrderDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .orderList(orderDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching user orders: " + e.getMessage())
                    .build();
        }
    }

    private OrderDto mapToOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setUserId(order.getUserId());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getOrderItemList() != null) {
            List<OrderItemDto> orderItemDtos = order.getOrderItemList().stream()
                    .map(this::mapToOrderItemDto)
                    .toList();
            dto.setOrderItemList(orderItemDtos);
        }

        return dto;
    }

    private OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setStatus(orderItem.getStatus());
        dto.setUserId(orderItem.getUserId());
        dto.setProductId(orderItem.getProductId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setCreatedAt(orderItem.getCreatedAt());
        return dto;
    }
}
