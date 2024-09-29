package com.example.demo1.services;

import com.example.demo1.models.Product;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    @Override
    public Product createProduct(ProductRequest productRequest) {
        MultipartFile image = productRequest.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime()+"_"+image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream() ) {
                Files.copy(inputStream,Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }

        Product newProduct = new Product();
        newProduct.setName(productRequest.getName());
        newProduct.setBrand(productRequest.getBrand());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setCreatedAt(createdAt);
        newProduct.setImage(storageFileName);
        newProduct.setCategory(productRequest.getCategory());
        return productRepository.save(newProduct);
    }

    @Override
    public Product updateProduct(Long id,ProductRequest productRequest) throws Exception {
        Product product = getProductById(id);

        if(!productRequest.getImageFile().isEmpty()) {
           String uploadDir = "public/images/";
           Path oldImagePath = Paths.get(uploadDir + product.getImage());
           try {
               Files.delete(oldImagePath);
           }catch (Exception e) {
               System.out.println("Exception: "+e.getMessage());
           }

           MultipartFile image = productRequest.getImageFile();
           Date createdAt = new Date();
           String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

           try (InputStream inputStream = image.getInputStream() ) {
               Files.copy(inputStream,Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
           }

           product.setImage(storageFileName);
        }
        product.setCategory(productRequest.getCategory());
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) throws Exception {
        Product deleteProduct = getProductById(id);
        productRepository.delete(deleteProduct);
    }

    @Override
    public Product getProductById(Long id) throws Exception {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            throw new Exception("product not found");
        }
        return product.get();
    }

    @Override
    public List<Product> searchProduct(String keyword) {
        return productRepository.findByName(keyword);
    }
}
