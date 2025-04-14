package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateDto {
    private String orderItemId;
    private String status;
    private UserDto user;
    private ProductDto product;
}