package com.example.demo1.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductImageRequest {

    private Long productId;

    private MultipartFile imageFile;
}
