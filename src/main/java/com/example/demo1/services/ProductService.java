package com.example.demo1.services;


import com.example.demo1.models.Product;
import com.example.demo1.models.ProductImage;
import com.example.demo1.request.ProductRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ProductService {
    public Page<Product> getAllProducts(int pageNumber);

    public Product createProduct(ProductRequest productRequest);

    public Product updateProduct(Long id, ProductRequest productRequest) throws Exception;

    public void deleteProduct(Long id) throws Exception;

    public Product getProductById(Long id) throws Exception;

    public List<Product> searchProduct(String name,
                                       String color,
                                       Long categoryId,
                                       List<String> features,
                                       LocalDate fromCreateAt,
                                       LocalDate toCreateAt);

    public Page<Product> searchProduct(String name,
                                       String color,
                                       Long categoryId,
                                       List<String> features,
                                       LocalDate fromCreateAt,
                                       LocalDate toCreateAt,
                                       int pageNumber);

    public boolean existsByName(String name);

    public List<ProductImage> getAllProductImage();

}