package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Category;
import com.pidan.blog.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<Category>> listCategories() {
        List<Category> categories = categoryService.listAll();
        return ApiResponse.success(categories);
    }
}
