package com.pidan.blog.repository;

import com.pidan.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findByPublishedTrue(Pageable pageable);

    List<Post> findByCategoryId(UUID categoryId);

    List<Post> findByPinnedTrueAndPublishedTrue();

    Page<Post> findByPublishedTrueOrderByPinnedDescCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.published = true")
    long countByPublishedTrue();

    @Query("SELECT COUNT(p) FROM Post p")
    long countAll();

    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Post> findByPublished(Boolean published, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND EXISTS (SELECT t FROM p.tags t WHERE t.slug = :tagSlug) ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> findByTagSlug(@Param("tagSlug") String tagSlug, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.category.slug = :categorySlug ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> findByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.slug = :categorySlug ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> searchByTitleAndCategorySlug(@Param("keyword") String keyword, @Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.published = true AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND EXISTS (SELECT t FROM p.tags t WHERE t.slug = :tagSlug) ORDER BY p.pinned DESC, p.createdAt DESC")
    Page<Post> searchByTitleAndTagSlug(@Param("keyword") String keyword, @Param("tagSlug") String tagSlug, Pageable pageable);

    List<Post> findByPublishedTrueOrderByCreatedAtDesc();
}
