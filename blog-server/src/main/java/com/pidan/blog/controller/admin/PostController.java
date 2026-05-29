package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.dto.PostDTO;
import com.pidan.blog.dto.PostResponse;
import com.pidan.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<Page<PostResponse>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean published) {
        Page<PostResponse> posts = postService.listAdmin(page, size, search, published);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPost(@PathVariable UUID id) {
        PostResponse post = postService.getById(id);
        return ApiResponse.success(post);
    }

    @PostMapping
    public ApiResponse<PostResponse> createPost(@Valid @RequestBody PostDTO postDTO) {
        PostResponse post = postService.create(postDTO);
        return ApiResponse.success("文章创建成功", post);
    }

    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(@PathVariable UUID id,
                                                 @Valid @RequestBody PostDTO postDTO) {
        PostResponse post = postService.update(id, postDTO);
        return ApiResponse.success("文章更新成功", post);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable UUID id) {
        postService.delete(id);
        return ApiResponse.success("文章删除成功", null);
    }
}
