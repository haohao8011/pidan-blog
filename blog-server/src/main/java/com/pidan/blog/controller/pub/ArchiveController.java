package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.dto.PostResponse;
import com.pidan.blog.repository.PostRepository;
import com.pidan.blog.entity.Post;
import com.pidan.blog.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/archive")
public class ArchiveController {

    private final PostRepository postRepository;

    public ArchiveController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public ApiResponse<Map<String, List<PostResponse>>> getArchive() {
        List<Post> posts = postRepository.findByPublishedTrueOrderByCreatedAtDesc();

        // 按年月分组
        Map<String, List<PostResponse>> archive = posts.stream()
                .collect(Collectors.groupingBy(
                        post -> {
                            LocalDateTime date = post.getCreatedAt();
                            return YearMonth.from(date).toString(); // "2024-01" 格式
                        },
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(post -> PostResponse.builder()
                                        .id(post.getId())
                                        .title(post.getTitle())
                                        .slug(post.getSlug())
                                        .createdAt(post.getCreatedAt())
                                        .build())
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return ApiResponse.success(archive);
    }
}
