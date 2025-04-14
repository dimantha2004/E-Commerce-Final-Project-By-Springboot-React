package edu.icet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequest {

    private long productId;
    private int quantity;

}
