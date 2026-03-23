package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.Category;
import com.chitalebandhu.chitalebandhu.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(Category category){
        categoryRepository.save(category);
    }

    public void removeCategory(String id){
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if(existingCategory.isPresent()){
            categoryRepository.deleteById(id);
        }
        else{
            throw new RuntimeException("Category not found");
        }
    }

    public void updateCategory(String id, String newCategory){
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if(existingCategory.isPresent()){
            existingCategory.get().setCategory(newCategory);
        }
        else{
            throw new RuntimeException("Category not found");
        }
    }

    public List<Category> getCategories(){
       List<Category> categories = categoryRepository.findAll();
       if(categories.isEmpty()){
           throw new RuntimeException("No categories present");
       }
       else{
           return categories;
       }
    }
}
