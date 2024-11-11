package com.example.demo1.services;

import com.example.demo1.models.Category;
import com.example.demo1.models.Product;
import com.example.demo1.models.ProductFeature;
import com.example.demo1.models.ProductImage;
import com.example.demo1.repository.CategoryRepository;
import com.example.demo1.repository.ProductImageRepository;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public Page<Product> getAllProducts(int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber-1,5, Sort.by("createdAt").descending());
        return productRepository.findAll(pageRequest);
    }


    @Override
    public Product createProduct(ProductRequest productRequest) {
        // Save thumbnail
        MultipartFile thumbnail = productRequest.getThumbnail();
        Date createdAt = new Date();
        String thumbnailFileName = createdAt.getTime() + "_" + thumbnail.getOriginalFilename();
        String uploadDir = "public/images/";

        // try with resource
        try {
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = thumbnail.getInputStream()) {
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
        newProduct.setColor(productRequest.getColor());

        Category category = categoryRepository.findById(productRequest.getCategoryId()).get();
        newProduct.setCategory(category);


        List<String> features = productRequest.getFeatures();
        List<ProductFeature> productFeatures = new ArrayList<>();
        for(String feature : features) {
            ProductFeature newProductFeature = new ProductFeature();
            newProductFeature.setProduct(newProduct);
            newProductFeature.setFeature(feature);
            productFeatures.add(newProductFeature);
        }
        newProduct.setFeatures(productFeatures);
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
        Category category = categoryRepository.findById(productRequest.getCategoryId()).get();
        product.setCategory(category);
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setColor(productRequest.getColor());

        List<String> features = productRequest.getFeatures();
        List<ProductFeature> productFeaturesOld = product.getFeatures();
        productFeaturesOld.clear();
        for(String feature:features) {
            ProductFeature newProductFeature = new ProductFeature();
            newProductFeature.setFeature(feature);
            newProductFeature.setProduct(product);
            productFeaturesOld.add(newProductFeature);
        }
        product.setFeatures(productFeaturesOld);


        // Update product image
        // Xử lý ảnh thêm mới
        List<MultipartFile> imageFiles = productRequest.getImageFiles();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (!imageFile.isEmpty()) {
                    String fileName = new Date().getTime() + "_" + imageFile.getOriginalFilename();
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + fileName), StandardCopyOption.REPLACE_EXISTING);
                    }
                    // Thêm ảnh mới vào sản phẩm
                    ProductImage newImage = new ProductImage();
                    newImage.setImageFile(fileName);
                    newImage.setProduct(product);
                    product.getProductImages().add(newImage);
                }
            }
        }

        // Xử lý ảnh cần xóa
        String[] imagesToDelete = productRequest.getListDelete();
        if (imagesToDelete != null) {
            for (String fileName : imagesToDelete) {
                // Xóa file khỏi server
                Path filePath = Paths.get(uploadDir + fileName);
                try {
                    Files.delete(filePath);
                } catch (Exception e) {
                    System.out.println("Exception while deleting file: " + e.getMessage());
                }
                // Xóa ảnh khỏi database
                productImageRepository.deleteProductImage(fileName, product.getId());
            }
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) throws Exception {
        Product deleteProduct = getProductById(id);

        // try with resource
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
    public List<Product> searchProduct(String name, String color, Long categoryId, List<String> features, LocalDate fromCreateAt, LocalDate toCreateAt) {

        if(name.isEmpty() && fromCreateAt == null && toCreateAt == null && color.isEmpty() && categoryId == null && features == null) {
            return productRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));
        }else if(features == null) {
            return productRepository.searchWithoutFeature(name,fromCreateAt,toCreateAt,color,categoryId);
        } else {
            return productRepository.search(name,fromCreateAt,toCreateAt,color,categoryId,features,features.size());
        }
    }
    @Override
    public Page<Product> searchProduct(String name, String color, Long categoryId, List<String> features, LocalDate fromCreateAt, LocalDate toCreateAt, int pageNumber) {

        List<Product> products = this.searchProduct(name,color,categoryId,features,fromCreateAt,toCreateAt);
        Pageable pageable = PageRequest.of(pageNumber-1,5);
        int start = (int) pageable.getOffset();
        int end = (int) ( (pageable.getOffset() + pageable.getPageSize()) > products.size() ?  products.size() : pageable.getOffset() + pageable.getPageSize());

        products = products.subList(start,end);

        return new PageImpl<Product>(products,pageable,this.searchProduct(name,color,categoryId,features,fromCreateAt,toCreateAt).size());
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
