package edu.icet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    @NotNull
    @Size(min = 1)
    private List<CheckoutItem> items;

    @NotBlank
    private String successUrl;

    @NotBlank
    private String cancelUrl;
}