package edu.icet.productservice.service;

import edu.icet.productservice.dto.ProductDto;
import edu.icet.productservice.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Response createProduct(Long categoryId, MultipartFile image, String name, String description, String price, String quantity);
    Response getAllProducts();
    Response getProductById(Long productId);
    Response getProductsByCategoryId(Long categoryId);
    Response searchProducts(String searchValue);
    Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, String price, String quantity);
    Response deleteProduct(Long productId);
}
