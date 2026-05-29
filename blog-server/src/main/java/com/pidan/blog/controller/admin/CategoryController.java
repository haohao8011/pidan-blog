package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Category;
import com.pidan.blog.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
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

    @GetMapping("/{id}")
    public ApiResponse<Category> getCategory(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        return ApiResponse.success(category);
    }

    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.create(category);
        return ApiResponse.success("分类创建成功", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable UUID id,
                                                 @RequestBody Category category) {
        Category updated = categoryService.update(id, category);
        return ApiResponse.success("分类更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.success("分类删除成功", null);
    }
}
