package com.pidan.blog.service;

import com.pidan.blog.common.SlugUtils;
import com.pidan.blog.dto.PostDTO;
import com.pidan.blog.dto.PostResponse;
import com.pidan.blog.entity.Category;
import com.pidan.blog.entity.Post;
import com.pidan.blog.entity.Tag;
import com.pidan.blog.repository.CategoryRepository;
import com.pidan.blog.repository.CommentRepository;
import com.pidan.blog.repository.PostRepository;
import com.pidan.blog.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final MarkdownService markdownService;

    public PostService(PostRepository postRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       CommentRepository commentRepository,
                       MarkdownService markdownService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.markdownService = markdownService;
    }

    public Page<PostResponse> listPublished(int page, int size, String search,
                                            String categorySlug, String tagSlug) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;

        if (search != null && !search.isBlank() && categorySlug != null && !categorySlug.isBlank()) {
            posts = postRepository.searchByTitleAndCategorySlug(search, categorySlug, pageable);
        } else if (search != null && !search.isBlank() && tagSlug != null && !tagSlug.isBlank()) {
            posts = postRepository.searchByTitleAndTagSlug(search, tagSlug, pageable);
        } else if (search != null && !search.isBlank()) {
            posts = postRepository.searchByTitle(search, pageable);
        } else if (categorySlug != null && !categorySlug.isBlank()) {
            posts = postRepository.findByCategorySlug(categorySlug, pageable);
        } else if (tagSlug != null && !tagSlug.isBlank()) {
            posts = postRepository.findByTagSlug(tagSlug, pageable);
        } else {
            posts = postRepository.findByPublishedTrueOrderByPinnedDescCreatedAtDesc(pageable);
        }

        return posts.map(this::convertToResponse);
    }

    public PostResponse getBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return convertToResponse(post);
    }

    @Transactional
    public PostResponse create(PostDTO dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setSlug(dto.getSlug() != null && !dto.getSlug().isBlank()
                ? dto.getSlug() : generateSlug(dto.getTitle()));
        post.setContent(dto.getContent());
        post.setContentHtml(markdownService.toHtml(dto.getContent()));
        post.setCoverImage(dto.getCoverImage());
        post.setPublished(dto.getPublished() != null ? dto.getPublished() : false);
        post.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        post.setViewCount(0L);
        post.setLikeCount(0L);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
            post.setCategory(category);
        }

        post = postRepository.save(post);

        // 处理标签
        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            Set<Tag> tags = resolveOrCreateTags(dto.getTagNames());
            post.setTags(tags);
            postRepository.save(post);
        }

        return convertToResponse(post);
    }

    @Transactional
    public PostResponse update(UUID id, PostDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        post.setTitle(dto.getTitle());
        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            post.setSlug(dto.getSlug());
        }
        post.setContent(dto.getContent());
        post.setContentHtml(markdownService.toHtml(dto.getContent()));
        post.setCoverImage(dto.getCoverImage());
        post.setPublished(dto.getPublished() != null ? dto.getPublished() : post.getPublished());
        post.setPinned(dto.getPinned() != null ? dto.getPinned() : post.getPinned());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }

        // 处理标签
        if (dto.getTagNames() != null) {
            Set<Tag> tags = resolveOrCreateTags(dto.getTagNames());
            post.getTags().clear();
            post.getTags().addAll(tags);
        }

        post = postRepository.save(post);
        return convertToResponse(post);
    }

    @Transactional
    public void delete(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("文章不存在");
        }
        postRepository.deleteById(id);
    }

    @Transactional
    public void like(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public Page<PostResponse> listAdmin(int page, int size, String search, Boolean published) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts;

        if (search != null && !search.isBlank() && published != null) {
            Page<Post> filtered = postRepository.findByPublished(published, pageable);
            posts = filtered.filter(p -> p.getTitle().toLowerCase().contains(search.toLowerCase()));
        } else if (search != null && !search.isBlank()) {
            posts = postRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (published != null) {
            posts = postRepository.findByPublished(published, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(this::convertToResponse);
    }

    public PostResponse getById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        return convertToResponse(post);
    }

    private String generateSlug(String title) {
        String slug = SlugUtils.generateSlug(title);
        return SlugUtils.ensureUnique(slug, postRepository::findBySlug);
    }

    private Set<Tag> resolveOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            if (tagName == null || tagName.isBlank()) continue;
            String tagSlug = SlugUtils.ensureUnique(
                    SlugUtils.generateSlug(tagName),
                    tagRepository::findBySlug
            );
            Tag tag = tagRepository.findBySlug(tagSlug)
                    .orElseGet(() -> tagRepository.save(
                            Tag.builder().name(tagName).slug(tagSlug).build()
                    ));
            tags.add(tag);
        }
        return tags;
    }

    private PostResponse convertToResponse(Post post) {
        long commentCount = commentRepository.countByPostIdAndApprovedTrue(post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .contentHtml(post.getContentHtml())
                .coverImage(post.getCoverImage())
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .tagNames(post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .published(post.getPublished())
                .pinned(post.getPinned())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .commentCount(commentCount)
                .build();
    }
}
