package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.dto.CommentDTO;
import com.pidan.blog.dto.PostResponse;
import com.pidan.blog.entity.Comment;
import com.pidan.blog.service.CommentService;
import com.pidan.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<Page<PostResponse>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {
        Page<PostResponse> posts = postService.listPublished(page, size, search, category, tag);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{slug}")
    public ApiResponse<PostResponse> getPost(@PathVariable String slug) {
        PostResponse post = postService.getBySlug(slug);
        return ApiResponse.success(post);
    }

    @PostMapping("/{slug}/like")
    public ApiResponse<Void> likePost(@PathVariable String slug) {
        postService.like(slug);
        return ApiResponse.success("点赞成功", null);
    }

    @PostMapping("/{slug}/comments")
    public ApiResponse<Comment> submitComment(@PathVariable String slug,
                                              @Valid @RequestBody CommentDTO commentDTO) {
        // 通过 slug 查找文章，设置 postId
        PostResponse post = postService.getBySlug(slug);
        commentDTO.setPostId(post.getId());
        Comment comment = commentService.create(commentDTO);
        return ApiResponse.success("评论提交成功，等待审核", comment);
    }
}
