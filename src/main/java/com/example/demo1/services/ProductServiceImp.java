package com.example.demo1.services;

import com.example.demo1.models.Product;
import com.example.demo1.models.ProductImage;
import com.example.demo1.repository.ProductImageRepository;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.request.ProductImageRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

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
        String uploadDir = "public/images/";

        try {
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

        List<MultipartFile> imageFiles = productRequest.getImageFiles();
        List<ProductImage> productImages = new ArrayList<>();
        for(MultipartFile imageFile: imageFiles) {
            if(!imageFile.isEmpty()) {
                String storageFileName = new Date().getTime() + "_" + imageFile.getOriginalFilename();
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }catch (IOException e) {
                    System.out.println("Exception:" + e.getMessage());
                }
                ProductImage newProductImage = new ProductImage();
                newProductImage.setImageFile(storageFileName);
                newProductImage.setProduct(newProduct);
                productImages.add(newProductImage);
            }
        }
        newProduct.setProductImages(productImages);
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

        // Update product image
        List<MultipartFile> validImageFiles = productRequest.getImageFiles();

        if(!validImageFiles.isEmpty()) {
            for(ProductImage productImage: product.getProductImages()) {
                try {
                    Files.delete(Paths.get(uploadDir + productImage.getImageFile()));
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }

            List<ProductImage> productImages = product.getProductImages();
            for(MultipartFile imageFile : productRequest.getImageFiles()) {
                if(!imageFile.isEmpty()) {
                    String storageFileName = new Date().getTime() + "_" + imageFile.getOriginalFilename();
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                    }
                    ProductImage newProductImage = new ProductImage();
                    newProductImage.setImageFile(storageFileName);
                    newProductImage.setProduct(product);
                    productImages.add(newProductImage);
                }
            }
            product.setProductImages(productImages);
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
            List<ProductImage> productImages = deleteProduct.getProductImages();
            for(ProductImage productImage : productImages) {
                Path imageFilePath = Paths.get("public/images/" + productImage.getImageFile());
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

    @Override
    public List<ProductImage> getAllProductImage() {
        return productImageRepository.findAll();
    }


}
