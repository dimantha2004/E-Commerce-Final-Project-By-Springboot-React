package edu.icet.productservice.controller;

import edu.icet.productservice.dto.CategoryDto;
import edu.icet.productservice.dto.Response;
import edu.icet.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Response> createCategory(@RequestBody CategoryDto categoryDto) {
        Response response = categoryService.createCategory(categoryDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllCategories() {
        Response response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-category-by-id/{categoryId}")
    public ResponseEntity<Response> getCategoryById(@PathVariable Long categoryId) {
        Response response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<Response> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto) {
        Response response = categoryService.updateCategory(categoryId, categoryDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Response> deleteCategory(@PathVariable Long categoryId) {
        Response response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }
}
