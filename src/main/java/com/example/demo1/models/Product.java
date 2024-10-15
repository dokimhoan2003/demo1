package com.example.demo1.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private String brand;

    private Date createdAt;

    private String thumbnail;

    @Column(length = 1000)
    @ElementCollection
    private List<String> imageFiles;

    private String color;

    private String features;

}
