package com.example.demo1.repository;

import com.example.demo1.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByCategoryName(String categoryName);
}
