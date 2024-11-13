package com.example.demo1.services;

import com.example.demo1.models.Category;
import com.example.demo1.models.Product;
import com.example.demo1.repository.CategoryRepository;
import com.example.demo1.request.CategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> getAllCategoriesWithPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber-1,5, Sort.by("id").descending());
        return categoryRepository.findAll(pageable);
    }


    @Override
    public Category createCategory(CategoryRequest categoryRequest) {
        Category newCategory = new Category();
        newCategory.setCategoryName(categoryRequest.getCategoryName());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest categoryRequest) throws Exception {
        Category category = this.getCategoryById(id);
        category.setCategoryName(categoryRequest.getCategoryName());
        return categoryRepository.save(category);

    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) throws Exception {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()) {
            throw new Exception("Category not found");
        }
        return categoryOptional.get();
    }

    @Override
    public boolean checkCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    public List<Category> searchCategory(String key) {
        if(key.isEmpty()) {
            return categoryRepository.findAll(Sort.by("id").descending());
        }else {
            return categoryRepository.searchCategory(key);
        }
    }

    @Override
    public Page<Category> searchCategory(String key, int pageNumber) {
        List<Category> categories = this.searchCategory(key);
        Pageable pageable = PageRequest.of(pageNumber-1,5);
        int start = (int) pageable.getOffset();
        int end = (int) ( (pageable.getOffset() + pageable.getPageSize()) > categories.size() ?  categories.size() : pageable.getOffset() + pageable.getPageSize());

        categories = categories.subList(start,end);

        return new PageImpl<Category>(categories,pageable,this.searchCategory(key).size());
    }
}
