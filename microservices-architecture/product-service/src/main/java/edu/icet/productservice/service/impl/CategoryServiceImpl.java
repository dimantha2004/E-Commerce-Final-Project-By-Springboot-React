package edu.icet.productservice.service.impl;

import edu.icet.productservice.dto.CategoryDto;
import edu.icet.productservice.dto.Response;
import edu.icet.productservice.entity.Category;
import edu.icet.productservice.repository.CategoryRepository;
import edu.icet.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Response createCategory(CategoryDto categoryDto) {
        try {
            Category category = new Category();
            category.setName(categoryDto.getName());

            Category savedCategory = categoryRepository.save(category);
            CategoryDto responseDto = mapToCategoryDto(savedCategory);

            return Response.builder()
                    .status(200)
                    .message("Category created successfully")
                    .category(responseDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error creating category: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDto> categoryDtos = categories.stream()
                    .map(this::mapToCategoryDto)
                    .toList();

            return Response.builder()
                    .status(200)
                    .categoryList(categoryDtos)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error fetching categories: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            CategoryDto categoryDto = mapToCategoryDto(category);

            return Response.builder()
                    .status(200)
                    .category(categoryDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(404)
                    .message("Category not found: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryDto) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            category.setName(categoryDto.getName());
            Category updatedCategory = categoryRepository.save(category);
            CategoryDto responseDto = mapToCategoryDto(updatedCategory);

            return Response.builder()
                    .status(200)
                    .message("Category updated successfully")
                    .category(responseDto)
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error updating category: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            categoryRepository.delete(category);

            return Response.builder()
                    .status(200)
                    .message("Category deleted successfully")
                    .build();

        } catch (Exception e) {
            return Response.builder()
                    .status(500)
                    .message("Error deleting category: " + e.getMessage())
                    .build();
        }
    }

    private CategoryDto mapToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}
