package edu.icet.service.impl;

import edu.icet.dto.CategoryDto;
import edu.icet.dto.Response;
import edu.icet.entity.Category;
import edu.icet.entity.Product;
import edu.icet.exception.NotFoundException;
import edu.icet.mapper.EntityDtoMapper;
import edu.icet.repository.CategoryRepository;
import edu.icet.repository.ProductRepository;
import edu.icet.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final ProductRepository ProductRepository;




    @Override
    public Response createCategory(CategoryDto categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("Category created successfully")
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("category updated successfully")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(entityDtoMapper::mapCategoryToDtoBasic)
                .collect(Collectors.toList());

        return  Response.builder()
                .status(200)
                .categoryList(categoryDtoList)
                .build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category Not Found"));
        CategoryDto categoryDto = entityDtoMapper.mapCategoryToDtoBasic(category);
        return Response.builder()
                .status(200)
                .category(categoryDto)
                .build();
    }

    @Transactional
    public Response deleteCategory(Long categoryId) {
        try {
            // Get products using instance method
            List<Product> products = ProductRepository.findByCategoryId(categoryId);

            // Disassociate products
            products.forEach(product -> product.setCategory(null));
            ProductRepository.saveAll(products);  // Use instance method

            // Delete category
            categoryRepo.deleteById(categoryId);

            return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Category deleted successfully")
                    .build();
        } catch (Exception e) {
            return Response.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Delete failed: " + e.getMessage())
                    .build();
        }
    }
}

