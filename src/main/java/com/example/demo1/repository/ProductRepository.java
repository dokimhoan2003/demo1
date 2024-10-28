package com.example.demo1.repository;

import com.example.demo1.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //search all
    @Query("SELECT p FROM Product p WHERE p.name = :name " +
            "OR DATE(p.createdAt) = :createAt " +
            "OR p.color = :color " +
            "OR p.category = :category " +
            "OR p.features LIKE %:featureConcat%")
    List<Product> search(@Param("name") String name,
                         @Param("createAt") LocalDate createAt,
                         @Param("color") String color,
                         @Param("category") String category,
                         @Param("featureConcat") String featureConcat);

    // search date
    @Query("SELECT p FROM Product p WHERE DATE(p.createdAt) = :createAt")
    List<Product> searchDate(@Param("createAt") LocalDate createAt);

    // search color
    @Query("SELECT p FROM Product p WHERE p.color = :color")
    List<Product> searchColor(@Param("color") String color);

    // search category
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> searchCategory(@Param("category") String category);

    // search feature
    @Query("SELECT p FROM Product p WHERE p.features LIKE %:featureConcat%")
    List<Product> searchFeature(@Param("featureConcat") String featureConcat);


    boolean existsByName(String name);
}
