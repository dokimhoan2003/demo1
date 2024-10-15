package com.example.demo1.services;

import com.example.demo1.models.Product;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    @Override
    public Product createProduct(ProductRequest productRequest) {
        // Save thumbnail
        MultipartFile thumbnail = productRequest.getThumbnail();
        Date createdAt = new Date();
        String thumbnailFileName = createdAt.getTime() + "_" + thumbnail.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = thumbnail.getInputStream() ) {
                Files.copy(inputStream,Paths.get(uploadDir+thumbnailFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }

        Product newProduct = new Product();
        newProduct.setName(productRequest.getName());
        newProduct.setBrand(productRequest.getBrand());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setCreatedAt(new Date());
        newProduct.setCategory(productRequest.getCategory());
        newProduct.setColor(productRequest.getColor());
        newProduct.setFeatures(productRequest.getFeatures());
        newProduct.setThumbnail(thumbnailFileName);

        // Save details image
        List<MultipartFile> images = productRequest.getImageFiles().stream().filter(image -> !image.isEmpty()).collect(Collectors.toList());
        List<String> imageSaves = new ArrayList<>();
        for(MultipartFile image: images) {
            String storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();
            try {
                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                imageSaves.add(storageFileName);
            }catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
            }
        }
        newProduct.setImageFiles(imageSaves);
        return productRepository.save(newProduct);
    }

    @Override
    public Product updateProduct(Long id,ProductRequest productRequest) throws Exception {

        // update thumbnail
        Product product = getProductById(id);
        String uploadDir = "public/images/";
        if(!productRequest.getThumbnail().isEmpty()) {

           Path oldThumbnailPath = Paths.get(uploadDir + product.getThumbnail());
           try {
               Files.delete(oldThumbnailPath);
           }catch (Exception e) {
               System.out.println("Exception: "+e.getMessage());
           }

           MultipartFile thumbnail = productRequest.getThumbnail();
           Date createdAt = new Date();
           String storageFileName = createdAt.getTime() + "_" + thumbnail.getOriginalFilename();

           try (InputStream inputStream = thumbnail.getInputStream() ) {
               Files.copy(inputStream,Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
           }

           product.setThumbnail(storageFileName);
        }

        // Update product info other
        product.setCategory(productRequest.getCategory());
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setColor(productRequest.getColor());
        product.setFeatures(productRequest.getFeatures());

        // Update details image
        List<MultipartFile> validImageFiles = productRequest.getImageFiles()
                .stream()
                .filter(file -> !file.isEmpty()) // lọc đi các file empty mặc định cuả trình duyệt
                .collect(Collectors.toList());
        if(!validImageFiles.isEmpty()) {
            for(String oldImage: product.getImageFiles()) {
                try {
                    Files.delete(Paths.get(uploadDir + oldImage));
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }

            List<String> newImages = new ArrayList<>();
            for(MultipartFile imageFile : productRequest.getImageFiles()) {
                if(!imageFile.isEmpty()) {
                    String imageFileName = new Date().getTime() + "_" + imageFile.getOriginalFilename();
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + imageFileName), StandardCopyOption.REPLACE_EXISTING);
                    }
                    newImages.add(imageFileName);
                }
            }
            product.setImageFiles(newImages);
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) throws Exception {
        Product deleteProduct = getProductById(id);
        try {
            //delete thumbnail đc save trên server
            Path thumbnailPath = Paths.get("public/images/" + deleteProduct.getThumbnail());
            try{
                Files.delete(thumbnailPath);
            }catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            //delete details image đc save trên server
            List<String> imageFiles = deleteProduct.getImageFiles();
            for(String imageFile : imageFiles) {
                Path imageFilePath = Paths.get("public/images/" + imageFile);
                try{
                    Files.delete(imageFilePath);
                }catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }

        }catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
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

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

}
