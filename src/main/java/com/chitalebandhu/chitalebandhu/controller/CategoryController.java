package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.entity.Category;
import com.chitalebandhu.chitalebandhu.services.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping("all")
    public List<Category> getAllCategories(){
        return categoryService.getCategories();
    }

    @PostMapping("add")
    public void addCategory(@RequestBody Category category){
        categoryService.addCategory(category);
    }

    @PutMapping("update/{id}")
    public void updateCategory(@PathVariable String id, @RequestBody String newCategory){
        categoryService.updateCategory(id, newCategory);
    }

    @DeleteMapping("delete/{id}")
    public void deleteCategory(@PathVariable String id){
        categoryService.removeCategory(id);
    }
}
