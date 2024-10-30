package com.example.demo1.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class SearchRequest {
    private String name;
    private List<String> features;
    private String color;
    private String category;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromCreateAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toCreateAt;
}
