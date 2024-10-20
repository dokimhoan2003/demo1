package com.example.demo1.controllers;

import com.example.demo1.models.Product;
import com.example.demo1.models.ProductImage;
import com.example.demo1.request.ProductImageRequest;
import com.example.demo1.request.ProductRequest;
import com.example.demo1.response.MessageResponse;
import com.example.demo1.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/check-name")
    @ResponseBody
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return new ResponseEntity<>(productService.existsByName(name), HttpStatus.OK);
    }



    @GetMapping("/{id}/confirm-delete")
    @ResponseBody
    public ResponseEntity<MessageResponse> confirmDelete(@PathVariable Long id) throws Exception {
            productService.deleteProduct(id);
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessage("Delete Successfully");
            return new ResponseEntity<>(messageResponse,HttpStatus.OK);
    }


    @PostMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProduct(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "products/index";
    }

    @GetMapping()
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {
        ProductRequest productRequest = new ProductRequest();
        model.addAttribute("productRequest",productRequest);
        return "products/create";
    }


    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductRequest productRequest,
                                BindingResult result, Model model) throws IOException {
//        if(productRequest.getImageFile().isEmpty()) {
//            result.addError(new FieldError("productRequest","imageFile","The image is required"));
//        }
//        if(productRequest.getImageFile().getSize() > 10*1024*1024) {
//            result.addError(new FieldError("productRequest","imageFile","File is too large! Maximum size is 10MB"));
//        }

        if (result.hasErrors()) {
            return "products/create";
        }
        Product product = productService.createProduct(productRequest);
        return "redirect:/products";
    }


    @GetMapping("/update/{id}")
    public String updateProduct(Model model,@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product",product);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(product.getName());
            productRequest.setPrice(product.getPrice());
            productRequest.setBrand(product.getBrand());
            productRequest.setDescription(product.getDescription());
            productRequest.setCategory(product.getCategory());
            productRequest.setFeatures(product.getFeatures());
            productRequest.setColor(product.getColor());

            model.addAttribute("productRequest",productRequest);

            return "products/update";
        }catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
            return "redirect:/products";
        }

    }

    @PostMapping("/update/{id}")
    public String updateProduct(Model model,
                                @PathVariable Long id,
                                @Valid @ModelAttribute ProductRequest productRequest,
                                BindingResult result) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product",product);

            productService.updateProduct(id, productRequest);
            return "redirect:/products";
        }catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
            return "products/update";
        }

    }


}
