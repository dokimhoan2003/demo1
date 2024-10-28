package com.example.demo1.controllers;

import com.example.demo1.models.Product;
import com.example.demo1.request.ProductRequest;
import com.example.demo1.request.SearchRequest;
import com.example.demo1.response.MessageResponse;
import com.example.demo1.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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



    @PostMapping("/search")
    public String searchProducts(@ModelAttribute SearchRequest searchRequest, Model model) {
        List<Product> products = productService.searchProduct(searchRequest);
        model.addAttribute("products", products);
        model.addAttribute("searchRequest",searchRequest);
        return "products/index";
    }

    @GetMapping()
    public String getAllProducts(@ModelAttribute SearchRequest searchRequest,Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products",products);
        model.addAttribute("searchRequest",searchRequest);
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

//    @PostMapping("/update/{id}")
//    public String updateProduct(Model model,
//                                @PathVariable Long id,
//                                @Valid @ModelAttribute ProductRequest productRequest,
//                                BindingResult result) {
//        try {
//            Product product = productService.getProductById(id);
//            model.addAttribute("product",product);
//
//            productService.updateProduct(id, productRequest);
//            return "redirect:/products";
//        }catch (Exception e) {
//            System.out.println("Exception" + e.getMessage());
//            return "products/update";
//        }
//    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductRequest productRequest,
            @RequestParam(value = "listImages", required = false) List<MultipartFile> listImages,
            @RequestParam(value = "listDelete", required = false) String[] listDelete
    ) throws Exception {
        // Cập nhật danh sách ảnh vào ProductRequest
        productRequest.setImageFiles(listImages);
        productRequest.setListDelete(listDelete);

        productService.updateProduct(id, productRequest);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Update successfully");
        return new ResponseEntity<>(messageResponse,HttpStatus.OK);
    }

    @GetMapping("/{id}/confirm-delete")
    @ResponseBody
    public ResponseEntity<MessageResponse> confirmDelete(@PathVariable Long id) throws Exception {
        productService.deleteProduct(id);
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Delete Successfully");
        return new ResponseEntity<>(messageResponse,HttpStatus.OK);
    }


}
