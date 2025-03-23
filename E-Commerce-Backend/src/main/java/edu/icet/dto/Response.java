package edu.icet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int status;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String token;
    private String role;
    private String expirationTime;

    @Builder.Default
    private int totalPage = 0;

    @Builder.Default
    private long totalElement = 0L;

    private AddressDto address;

    private UserDto user;
    private List<UserDto> userList;

    private CategoryDto category;
    private List<CategoryDto> categoryList;

    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;

    private OrderDto order;
    private List<OrderDto> orderList;

    private ProductDto product;
    private List<ProductDto> productList;
}