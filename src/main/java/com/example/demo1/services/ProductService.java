package com.example.demo1.services;


import com.example.demo1.models.Product;
import com.example.demo1.request.ProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();

    public Product createProduct(ProductRequest productRequest);

    public Product updateProduct(Long id, ProductRequest productRequest) throws Exception;

    public void deleteProduct(Long id) throws Exception;

    public Product getProductById(Long id) throws Exception;

    public List<Product> searchProduct(String keyword);

    public boolean existsByName(String name);

}