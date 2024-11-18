package com.example.demo1.controllers;

import com.example.demo1.models.Category;
import com.example.demo1.request.CategoryRequest;
import com.example.demo1.response.MessageResponse;
import com.example.demo1.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/check-name")
    @ResponseBody
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return new ResponseEntity<>(categoryService.checkCategoryName(name), HttpStatus.OK);
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(name = "key", required = false, defaultValue = "") String key,
                         @RequestParam(defaultValue = "1") int page) {
        Page<Category> categories = categoryService.searchCategory(key,page);
        model.addAttribute("key",key);
        model.addAttribute("categories",categories);
        model.addAttribute("totalPages",categories.getTotalPages());
        model.addAttribute("currentPage",page);
        return "categories/list";
    }

    @GetMapping("")
    public String getAllCategories(Model model,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(name = "key", required = false, defaultValue = "") String key) {
        Page<Category> categories = categoryService.getAllCategoriesWithPage(page);
        model.addAttribute("key",key);
        model.addAttribute("categories",categories);
        model.addAttribute("totalPages",categories.getTotalPages());
        model.addAttribute("currentPage",page);
        return "categories/list";
    }

    @GetMapping("/create")
    public String createCategory(Model model) {
        CategoryRequest categoryRequest = new CategoryRequest();
        model.addAttribute("categoryRequest",categoryRequest);
        return "categories/create";
    }

    @PostMapping("/create")
    public String handleCreateCategory(Model model,
                                       @ModelAttribute("categoryRequest") CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        model.addAttribute("categoryRequest",categoryRequest);
        return "redirect:/categories";
    }

    @GetMapping("/update/{id}")
    public String updateCategory(Model model,@PathVariable Long id) {
        try{
            Category category = categoryService.getCategoryById(id);
            model.addAttribute("category",category);

            CategoryRequest categoryRequest = new CategoryRequest();
            categoryRequest.setCategoryName(category.getCategoryName());
            model.addAttribute("categoryRequest",categoryRequest);

            return "categories/update";
        }catch (Exception e) {
            return "homes/error";
        }
    }

    @PostMapping("/update/{id}")
    public String handleUpdateCategory(Model model,
                                       @PathVariable Long id,
                                       @ModelAttribute CategoryRequest categoryRequest) {
        try {
            categoryService.updateCategory(id,categoryRequest);
            model.addAttribute("categoryRequest",categoryRequest);
            return "redirect:/categories";
        }catch (Exception e) {
            return "categories/list";
        }

    }

    @GetMapping("/{id}/confirm-delete")
    @ResponseBody
    public ResponseEntity<MessageResponse> confirmDelete(@PathVariable Long id) {
        MessageResponse messageResponse = new MessageResponse();
        try {
            categoryService.deleteCategory(id);
            messageResponse.setMessage("Delete Successfully");
            return new ResponseEntity<>(messageResponse, HttpStatus.OK);
        }catch (Exception e) {
            messageResponse.setMessage("Delete Fail");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }

    }




}
