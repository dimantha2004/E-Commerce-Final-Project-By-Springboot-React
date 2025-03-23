package edu.icet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CheckoutItem {
    @NotBlank
    private String productId;

    @Positive
    private long price;

    @Min(1)
    private int quantity;
}