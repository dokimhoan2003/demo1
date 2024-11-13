package com.example.demo1.services;

import com.example.demo1.models.Category;
import com.example.demo1.request.CategoryRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    public Page<Category> getAllCategoriesWithPage(int pageNumber);
    public List<Category> getAllCategories();
    public Category createCategory(CategoryRequest categoryRequest);
    public Category updateCategory(Long id, CategoryRequest categoryRequest) throws Exception;
    public void deleteCategory(Long id);
    public Category getCategoryById(Long id) throws Exception;
    public boolean checkCategoryName(String categoryName);
    public List<Category> searchCategory(String key);
    public Page<Category> searchCategory(String key,int pageNumber);
}
