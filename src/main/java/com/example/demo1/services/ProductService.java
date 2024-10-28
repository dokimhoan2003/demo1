package com.example.demo1.services;


import com.example.demo1.models.Product;
import com.example.demo1.models.ProductImage;
import com.example.demo1.request.ProductRequest;
import com.example.demo1.request.SearchRequest;

import java.util.List;

public interface ProductService {
    public List<Product> getAllProducts();

    public Product createProduct(ProductRequest productRequest);

    public Product updateProduct(Long id, ProductRequest productRequest) throws Exception;

    public void deleteProduct(Long id) throws Exception;

    public Product getProductById(Long id) throws Exception;

    public List<Product> searchProduct(SearchRequest searchRequest);

    public boolean existsByName(String name);

    public List<ProductImage> getAllProductImage();

}