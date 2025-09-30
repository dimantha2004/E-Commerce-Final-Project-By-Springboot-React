package edu.icet.productservice.dto;

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
    private ProductDto product;
    private List<ProductDto> productList;
    private CategoryDto category;
    private List<CategoryDto> categoryList;
}
