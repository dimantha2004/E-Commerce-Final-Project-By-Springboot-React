package edu.icet.productservice.service;

import edu.icet.productservice.dto.CategoryDto;
import edu.icet.productservice.dto.Response;

public interface CategoryService {
    Response createCategory(CategoryDto categoryDto);
    Response getAllCategories();
    Response getCategoryById(Long categoryId);
    Response updateCategory(Long categoryId, CategoryDto categoryDto);
    Response deleteCategory(Long categoryId);
}
