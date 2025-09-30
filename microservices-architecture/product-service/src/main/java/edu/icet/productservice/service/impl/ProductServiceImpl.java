package edu.icet.productservice.service.impl;

import edu.icet.productservice.dto.ProductDto;
import edu.icet.productservice.dto.Response;
import edu.icet.productservice.entity.Category;
import edu.icet.productservice.entity.Product;
import edu.icet.productservice.repository.CategoryRepository;
import edu.icet.productservice.repository.ProductRepository;
import edu.icet.productservice.service.AwsS3Service;
import edu.icet.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AwsS3Service awsS3Service;

    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, String price, String quantity) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            String imageUrl = awsS3Service.saveImageToS3(image);

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setImageUrl(imageUrl);
            product.setPrice(new BigDecimal(price));
            product.setQuantity(Integer.parseInt(quantity));
            product.setCategory(category);

            Product savedProduct = productRepository.save(product);
            ProductDto productDto = mapToProductDto(savedProduct);

            return Response.builder()
                    .status(200)
                    .message("Product created successfully")
                    .product(productDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error creating product: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductDto> productDtos = products.stream()
                    .map(this::mapToProductDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .productList(productDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching products: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getProductById(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductDto productDto = mapToProductDto(product);

            return Response.builder()
                    .status(200)
                    .product(productDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(404)
                    .message("Product not found: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getProductsByCategoryId(Long categoryId) {
        try {
            List<Product> products = productRepository.findByCategoryId(categoryId);
            List<ProductDto> productDtos = products.stream()
                    .map(this::mapToProductDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .productList(productDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching products: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response searchProducts(String searchValue) {
        try {
            List<Product> products = productRepository.findByNameContaining(searchValue);
            List<ProductDto> productDtos = products.stream()
                    .map(this::mapToProductDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .productList(productDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error searching products: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, String price, String quantity) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (image != null && !image.isEmpty()) {
                String imageUrl = awsS3Service.saveImageToS3(image);
                product.setImageUrl(imageUrl);
            }

            product.setName(name);
            product.setDescription(description);
            product.setPrice(new BigDecimal(price));
            product.setQuantity(Integer.parseInt(quantity));
            product.setCategory(category);

            Product updatedProduct = productRepository.save(product);
            ProductDto productDto = mapToProductDto(updatedProduct);

            return Response.builder()
                    .status(200)
                    .message("Product updated successfully")
                    .product(productDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error updating product: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response deleteProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            productRepository.delete(product);

            return Response.builder()
                    .status(200)
                    .message("Product deleted successfully")
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error deleting product: " + e.getMessage())
                    .build();
        }
    }

    private ProductDto mapToProductDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }
}
