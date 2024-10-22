package com.example.demo1.repository;


import com.example.demo1.models.ProductImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_image WHERE image_file = :imageName AND product_id = :productId", nativeQuery = true)
    void deleteProductImage(@Param("imageName") String imageName, @Param("productId") Long productId);
}

