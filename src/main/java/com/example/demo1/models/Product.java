package com.example.demo1.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


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

    private String image;

    private String color;

    private String features;

}
