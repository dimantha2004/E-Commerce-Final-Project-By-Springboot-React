package edu.icet.dto;

import lombok.Data;

@Data

public class OrderItemRequest {

    private long productId;
    private int quantity;

}
