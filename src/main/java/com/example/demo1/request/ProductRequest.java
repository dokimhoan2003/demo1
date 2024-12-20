package com.example.demo1.request;

import com.example.demo1.models.Category;
import com.example.demo1.models.ProductFeature;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
public class ProductRequest {

//    @Size(min=1, max=25,message = "The name must be between  1 and 25 characters")
    private String name;

//    @Min(value= 0,message = "Price must be greater than or equal zero")
    private double price;

//    @Size(min=2, max=2000,message = "The description must be between  2 and 2000 characters")
    private String description;

//    @NotEmpty(message = "Category cannot be empty")
    private Long categoryId;

//    @Size(min=1, max=25,message = "The brand must be between  1 and 25 characters")
    private String brand;

    private MultipartFile thumbnail;

    private List<MultipartFile> imageFiles;

    private String[] listDelete;

//    @NotEmpty(message = "Please select value")
    private String color;

    private List<String> features;
}
