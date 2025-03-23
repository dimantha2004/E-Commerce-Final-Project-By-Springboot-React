package edu.icet.repository;

import edu.icet.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);  // Correct repository method
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
}