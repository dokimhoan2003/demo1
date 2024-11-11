package com.example.demo1.repository;

import com.example.demo1.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //Pageable
    @Query("SELECT p FROM Product p")
    Page<Product> findAll(Pageable pageable);

    //search all field
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.features f " +
            "WHERE (:features IS NULL OR f.feature IN :features) " +
            "AND (:name IS NULL OR :name = '' OR  p.name LIKE %:name%) " +
            "AND (:fromCreateAt IS NULL OR DATE(p.createdAt) >= :fromCreateAt) " +
            "AND (:toCreateAt IS NULL OR DATE(p.createdAt) <= :toCreateAt) " +
            "AND (:color IS NULL OR :color = '' OR p.color = :color) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "GROUP BY p.id " +
            "HAVING COUNT(f.feature) >= :featureCount ORDER BY p.createdAt DESC")
    List<Product> search(
            @Param("name") String name,
            @Param("fromCreateAt") LocalDate fromCreateAt,
            @Param("toCreateAt") LocalDate toCreateAt,
            @Param("color") String color,
            @Param("categoryId") Long categoryId,
            @Param("features") List<String> features,
            @Param("featureCount") int featureCount);

    //search without feature
    @Query("SELECT p FROM Product p " +
            "JOIN p.features f " +
            "WHERE (:name IS NULL OR :name = '' OR p.name LIKE %:name%) " +
            "AND (:fromCreateAt IS NULL OR DATE(p.createdAt) >= :fromCreateAt) " +
            "AND (:toCreateAt IS NULL OR DATE(p.createdAt) <= :toCreateAt) " +
            "AND (:color IS NULL OR :color = '' OR  p.color = :color) " +
            "AND (:categoryId IS NULL  OR p.category.id = :categoryId) ORDER BY p.createdAt DESC")
    List<Product> searchWithoutFeature(
            @Param("name") String name,
            @Param("fromCreateAt") LocalDate fromCreateAt,
            @Param("toCreateAt") LocalDate toCreateAt,
            @Param("color") String color,
            @Param("categoryId") Long categoryId);

    boolean existsByName(String name);
}
