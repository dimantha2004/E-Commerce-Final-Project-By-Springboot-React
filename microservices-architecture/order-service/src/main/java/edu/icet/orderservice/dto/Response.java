package edu.icet.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int status;
    private String message;
    private OrderDto order;
    private List<OrderDto> orderList;
    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;
}
