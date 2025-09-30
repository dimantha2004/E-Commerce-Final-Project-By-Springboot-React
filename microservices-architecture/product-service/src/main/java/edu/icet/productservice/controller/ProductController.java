package edu.icet.productservice.controller;

import edu.icet.productservice.dto.Response;
import edu.icet.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Response> createProduct(
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String quantity) {

        Response response = productService.createProduct(categoryId, image, name, description, price, quantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllProducts() {
        Response response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-product-by-id/{productId}")
    public ResponseEntity<Response> getProductById(@PathVariable Long productId) {
        Response response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-products-by-category-id/{categoryId}")
    public ResponseEntity<Response> getProductsByCategoryId(@PathVariable Long categoryId) {
        Response response = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchProducts(@RequestParam String searchValue) {
        Response response = productService.searchProducts(searchValue);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<Response> updateProduct(
            @PathVariable Long productId,
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String quantity) {

        Response response = productService.updateProduct(productId, categoryId, image, name, description, price, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long productId) {
        Response response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }
}
