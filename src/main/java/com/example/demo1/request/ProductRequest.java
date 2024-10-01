package com.example.demo1.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProductRequest {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Min(value= 0,message = "Price must be greater than or equal zero")
    private double price;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotEmpty(message = "Category cannot be empty")
    private String category;

    @NotEmpty(message = "Brand cannot be empty")
    private String brand;

    private MultipartFile imageFile;

    @NotEmpty(message = "Please select value")
    private String color;

    private String features;
}
