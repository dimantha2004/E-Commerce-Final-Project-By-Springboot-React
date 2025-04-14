package edu.icet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.icet.entity.OrderItem;
import edu.icet.entity.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private BigDecimal totalPrice;
    private List<OrderItemRequest>items;
    private Payment paymentInfo;
}
