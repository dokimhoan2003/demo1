package com.example.demo1.repository;

import com.example.demo1.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("SELECT c FROM Category c")
    Page<Category> findAll(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE %:key% ORDER BY c.id DESC")
    List<Category> searchCategory(@Param("key") String key);

    boolean existsByCategoryName(String categoryName);
}
