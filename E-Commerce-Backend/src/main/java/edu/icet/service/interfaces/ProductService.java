package edu.icet.service.interfaces;

import edu.icet.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface ProductService {

    Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price,Integer quantity);
    Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price,Integer quantity);
    Response deleteProduct(Long productId);
    Response getProductById(Long productId);
    Response getAllProducts();
    Response getProductsByCategory(Long categoryId);
    Response searchProduct(String searchValue);
}

