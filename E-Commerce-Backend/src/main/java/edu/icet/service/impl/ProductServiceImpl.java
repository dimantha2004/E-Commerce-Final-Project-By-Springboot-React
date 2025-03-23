package edu.icet.service.impl;

import edu.icet.dto.ProductDto;
import edu.icet.dto.Response;
import edu.icet.entity.Category;
import edu.icet.entity.Product;
import edu.icet.exception.NotFoundException;
import edu.icet.mapper.EntityDtoMapper;
import edu.icet.repository.CategoryRepository;
import edu.icet.repository.OrderItemRepository;
import edu.icet.repository.ProductRepository;
import edu.icet.service.Awss3Service;
import edu.icet.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final Awss3Service awsS3Service;
    private final OrderItemRepository OrderItemRepository;


    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price,Integer quantity) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        String productImageUrl = awsS3Service.saveImageToS3(image);

        Product product = new Product();
        product.setCategory(category);
        product.setPrice(price);
        product.setName(name);
        product.setDescription(description);
        product.setImageUrl(productImageUrl);
        product.setQuantity(quantity);

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Product successfully created")
                .build();
    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price, Integer quantity) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));

        Category category = null;
        String productImageUrl = null;

        if(categoryId != null) {
            category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        }
        if (image != null && !image.isEmpty()) {
            productImageUrl = awsS3Service.saveImageToS3(image);
        }

        if (category != null) product.setCategory(category);
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (productImageUrl != null) product.setImageUrl(productImageUrl);
        if (quantity != null) product.setQuantity(quantity); // Add this line to update quantity

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Product updated successfully")
                .build();
    }

    @Transactional
    public Response deleteProduct(Long productId) {
        try {
            // Delete related order items using instance method
            OrderItemRepository.deleteByProductId(productId);

            // Delete product
            productRepo.deleteById(productId);

            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Product deleted successfully")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Delete failed: " + e.getMessage())
                    .build();
        }
    }


    @Override
    public Response getProductById(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product Not Found"));
        ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(product);

        return Response.builder()
                .status(200)
                .product(productDto)
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<ProductDto> productList = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productList)
                .build();

    }

    @Override
    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);
        if(products.isEmpty()){
            throw new NotFoundException("No Products found for this category");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();

    }

    @Override
    public Response searchProduct(String searchValue) {
        List<Product> products = productRepo.findByNameContainingOrDescriptionContaining(searchValue, searchValue);

        if (products.isEmpty()){
            throw new NotFoundException("No Products Found");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .collect(Collectors.toList());


        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }
}