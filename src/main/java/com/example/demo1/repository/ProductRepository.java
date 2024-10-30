package com.example.demo1.repository;

import com.example.demo1.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //search all field
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.features f " +
            "WHERE (:features IS NULL OR f.feature IN :features) " +
            "AND (:name IS NULL OR :name = '' OR  p.name LIKE %:name%) " +
            "AND (:fromCreateAt IS NULL OR DATE(p.createdAt) >= :fromCreateAt) " +
            "AND (:toCreateAt IS NULL OR DATE(p.createdAt) <= :toCreateAt) " +
            "AND (:color IS NULL OR :color = '' OR p.color = :color) " +
            "AND (:category IS NULL OR :category = '' OR p.category = :category) " +
            "GROUP BY p.id " +
            "HAVING COUNT(f.feature) >= :featureCount ORDER BY p.createdAt DESC")
    List<Product> search(
            @Param("name") String name,
            @Param("fromCreateAt") LocalDate fromCreateAt,
            @Param("toCreateAt") LocalDate toCreateAt,
            @Param("color") String color,
            @Param("category") String category,
            @Param("features") List<String> features,
            @Param("featureCount") int featureCount);

    //search without feature
    @Query("SELECT p FROM Product p " +
            "JOIN p.features f " +
            "WHERE (:name IS NULL OR :name = '' OR p.name LIKE %:name%) " +
            "AND (:fromCreateAt IS NULL OR DATE(p.createdAt) >= :fromCreateAt) " +
            "AND (:toCreateAt IS NULL OR DATE(p.createdAt) <= :toCreateAt) " +
            "AND (:color IS NULL OR :color = '' OR  p.color = :color) " +
            "AND (:category IS NULL OR :category = '' OR p.category = :category) ORDER BY p.createdAt DESC")
    List<Product> searchWithoutFeature(
            @Param("name") String name,
            @Param("fromCreateAt") LocalDate fromCreateAt,
            @Param("toCreateAt") LocalDate toCreateAt,
            @Param("color") String color,
            @Param("category") String category);



//     //search date
//    @Query("SELECT p FROM Product p WHERE DATE(p.createdAt) >= :fromCreateAt AND DATE(p.createdAt) <= :toCreateAt")
//    List<Product> searchDate(@Param("fromCreateAt") LocalDate fromCreateAt,@Param("toCreateAt") LocalDate toCreateAt);
//
//    // search color
//    @Query("SELECT p FROM Product p WHERE p.color = :color")
//    List<Product> searchColor(@Param("color") String color);
//
//    // search category
//    @Query("SELECT p FROM Product p WHERE p.category = :category")
//    List<Product> searchCategory(@Param("category") String category);
//
//    // search feature
//    @Query("SELECT p FROM Product p JOIN p.features f WHERE f.feature IN :features " +
//            "GROUP BY p.id HAVING COUNT(f) >= :featureCount")
//    List<Product> searchFeature(@Param("features") List<String> features, @Param("featureCount") int featureCount);


    boolean existsByName(String name);
}
