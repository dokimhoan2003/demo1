package com.example.demo1.controllers;

import com.example.demo1.models.*;
import com.example.demo1.request.ProductRequest;
import com.example.demo1.response.MessageResponse;
import com.example.demo1.services.CategoryService;
import com.example.demo1.services.NotificationService;
import com.example.demo1.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/check-name")
    @ResponseBody
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return new ResponseEntity<>(productService.existsByName(name), HttpStatus.OK);
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(name = "name",required = false, defaultValue = "") String name,
                                 @RequestParam(name = "color",required = false, defaultValue = "") String color,
                                 @RequestParam(name = "categoryId",required = false) Long categoryId,
                                 @RequestParam(name = "features",required = false) List<String> features,
                                 @RequestParam(name = "fromCreateAt",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromCreateAt,
                                 @RequestParam(name = "toCreateAt",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toCreateAt,
                                 @RequestParam(defaultValue = "1") int page,
                                 Model model) {

        Page<Product> products = productService.searchProduct(name, color, categoryId, features, fromCreateAt, toCreateAt, page);

        model.addAttribute("name",name);
        model.addAttribute("color",color);
        model.addAttribute("categoryId",categoryId);
        model.addAttribute("features",features);
        model.addAttribute("fromCreateAt",fromCreateAt);
        model.addAttribute("toCreateAt",toCreateAt);

        model.addAttribute("products", products);
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("totalPages",products.getTotalPages());
        model.addAttribute("currentPage",page);
        return "products/index";
    }


    @GetMapping()
    public String getAllProducts(@RequestParam(name = "name", required = false, defaultValue = "") String name,
                                 @RequestParam(name = "color", required = false, defaultValue = "") String color,
                                 @RequestParam(name = "categoryId",required = false) Long categoryId,
                                 @RequestParam(name = "features", required = false) List<String> features,
                                 @RequestParam(name = "fromCreateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromCreateAt,
                                 @RequestParam(name = "toCreateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toCreateAt,
                                 @RequestParam(defaultValue = "1") int page,
                                 Model model) {

        Page<Product> products = productService.getAllProducts(page);

//        boolean hasSearchCriteria = !name.isEmpty() ||
//                !color.isEmpty() ||
//                !category.isEmpty() ||
//                (features != null && !features.isEmpty()) ||
//                fromCreateAt != null ||
//                toCreateAt != null;
//
//        if (hasSearchCriteria) {
//            products = productService.searchProduct(name, color, category, features, fromCreateAt, toCreateAt, page);
//        } else {
//            products = productService.getAllProducts(page);
//        }

        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("features", features);
        model.addAttribute("fromCreateAt", fromCreateAt);
        model.addAttribute("toCreateAt", toCreateAt);

        model.addAttribute("products", products);
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", page);

        model.addAttribute("notifications",notificationService.getAllNotification());

        return "products/index";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {
        ProductRequest productRequest = new ProductRequest();
        model.addAttribute("productRequest",productRequest);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories",categories);

        model.addAttribute("notifications",notificationService.getAllNotification());

        return "products/create";
    }


    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductRequest productRequest,
                                BindingResult result, Model model) throws IOException {

        Product createdProduct = productService.createProduct(productRequest);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
        String notificationCreateProduct = customerUserDetails.getUsername()
                + " created "
                + " product"
                + " with id: "
                + createdProduct.getId();
        Map<String, String> message = new HashMap<>();
        message.put("content", notificationCreateProduct);
        template.convertAndSend("/topic/notificationCreateProduct", message);
        Notification notification = new Notification();
        notification.setContent(notificationCreateProduct);
        notification.setCreatedAt(LocalDateTime.now());
        notificationService.saveNotification(notification);

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
            productRequest.setCategoryId(product.getCategory().getId());

            List<String> features = new ArrayList<>();
            List<ProductFeature> productFeatures = product.getFeatures();
            for(ProductFeature productFeature : productFeatures) {
                String feature = productFeature.getFeature();
                features.add(feature);
            }
            productRequest.setFeatures(features);
            productRequest.setColor(product.getColor());

            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories",categories);
            model.addAttribute("productRequest",productRequest);
            model.addAttribute("notifications",notificationService.getAllNotification());


            return "products/update";
        }catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
            return "redirect:/products";
        }

    }


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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
        String notificationUpdateProduct = customerUserDetails.getUsername()
                +" edit product with id: "
                + id;
        Map<String, String> message = new HashMap<>();
        message.put("content", notificationUpdateProduct);
        template.convertAndSend("/topic/notificationUpdateProduct", message);
        Notification notification = new Notification();
        notification.setContent(notificationUpdateProduct);
        notification.setCreatedAt(LocalDateTime.now());
        notificationService.saveNotification(notification);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Update successfully");
        return new ResponseEntity<>(messageResponse,HttpStatus.OK);
    }

    @GetMapping("/{id}/confirm-delete")
    @ResponseBody
    public ResponseEntity<MessageResponse> confirmDelete(@PathVariable Long id) {
        MessageResponse messageResponse = new MessageResponse();
        try {
            productService.deleteProduct(id);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
            String notificationDeleteProduct = customerUserDetails.getUsername()
                    +" has changed product status with id: "
                    + id;
            Map<String, String> message = new HashMap<>();
            message.put("content", notificationDeleteProduct);
            template.convertAndSend("/topic/notificationDeleteProduct", message);
            Notification notification = new Notification();
            notification.setContent(notificationDeleteProduct);
            notification.setCreatedAt(LocalDateTime.now());
            notificationService.saveNotification(notification);

            messageResponse.setMessage("Delete Successfully");
            return new ResponseEntity<>(messageResponse,HttpStatus.OK);
        }catch (Exception e) {
            messageResponse.setMessage("Delete Fail");
            return new ResponseEntity<>(messageResponse,HttpStatus.NOT_FOUND);
        }

    }


}
