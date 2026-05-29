package com.pidan.blog.service;

import com.pidan.blog.entity.Category;
import com.pidan.blog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listAll() {
        return categoryRepository.findBySlugIsNotNullOrderBySortOrder();
    }

    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }

    @Transactional
    public Category create(Category category) {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(generateSlug(category.getName()));
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID id, Category updatedCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));

        category.setName(updatedCategory.getName());
        if (updatedCategory.getSlug() != null && !updatedCategory.getSlug().isBlank()) {
            category.setSlug(updatedCategory.getSlug());
        }
        category.setDescription(updatedCategory.getDescription());
        category.setSortOrder(updatedCategory.getSortOrder());

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("分类不存在");
        }
        categoryRepository.deleteById(id);
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s-]");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slug.isEmpty()) {
            slug = "category-" + System.currentTimeMillis();
        }

        String originalSlug = slug;
        int counter = 1;
        while (categoryRepository.findBySlug(slug).isPresent()) {
            slug = originalSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
