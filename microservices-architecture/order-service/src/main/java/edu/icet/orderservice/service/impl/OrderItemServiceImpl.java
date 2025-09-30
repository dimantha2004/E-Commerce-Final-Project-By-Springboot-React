package edu.icet.orderservice.service.impl;

import edu.icet.orderservice.dto.OrderItemDto;
import edu.icet.orderservice.dto.Response;
import edu.icet.orderservice.entity.OrderItem;
import edu.icet.orderservice.enums.OrderStatus;
import edu.icet.orderservice.repository.OrderItemRepository;
import edu.icet.orderservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public Response placeOrder(OrderItemDto orderItemDto) {
        try {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(orderItemDto.getQuantity());
            orderItem.setPrice(orderItemDto.getPrice());
            orderItem.setStatus(OrderStatus.PENDING);
            orderItem.setUserId(orderItemDto.getUserId());
            orderItem.setProductId(orderItemDto.getProductId());

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            OrderItemDto responseDto = mapToOrderItemDto(savedOrderItem);

            return Response.builder()
                    .status(200)
                    .message("Order placed successfully")
                    .orderItem(responseDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error placing order: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        try {
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found"));

            orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
            OrderItemDto responseDto = mapToOrderItemDto(updatedOrderItem);

            return Response.builder()
                    .status(200)
                    .message("Order status updated successfully")
                    .orderItem(responseDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error updating order status: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response filterOrderItems(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderItem> orderItems = orderItemRepository.findByStatus(orderStatus);
            List<OrderItemDto> orderItemDtos = orderItems.stream()
                    .map(this::mapToOrderItemDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .orderItemList(orderItemDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error filtering orders: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getAllOrderItems() {
        try {
            List<OrderItem> orderItems = orderItemRepository.findAll();
            List<OrderItemDto> orderItemDtos = orderItems.stream()
                    .map(this::mapToOrderItemDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .orderItemList(orderItemDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching order items: " + e.getMessage())
                    .build();
        }
    }

    private OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setStatus(orderItem.getStatus());
        dto.setUserId(orderItem.getUserId());
        dto.setProductId(orderItem.getProductId());
        dto.setOrderId(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null);
        dto.setCreatedAt(orderItem.getCreatedAt());
        return dto;
    }
}
