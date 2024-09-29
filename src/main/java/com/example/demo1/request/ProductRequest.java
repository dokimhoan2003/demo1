package com.example.demo1.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class ProductRequest {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Min(0)
    private double price;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotEmpty(message = "Category cannot be empty")
    private String category;

    @NotEmpty(message = "Brand cannot be empty")
    private String brand;

    private MultipartFile imageFile;
}
