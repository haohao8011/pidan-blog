package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Comment;
import com.pidan.blog.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<Page<Comment>> listComments(
            @RequestParam(required = false) UUID postId,
            @RequestParam(required = false) Boolean approved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments;
        if (postId != null) {
            comments = commentService.listAllByApproved(approved, pageable);
        } else {
            comments = commentService.listAllByApproved(approved, pageable);
        }
        return ApiResponse.success(comments);
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<Comment> approveComment(@PathVariable UUID id) {
        Comment comment = commentService.approve(id);
        return ApiResponse.success("评论审核通过", comment);
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<Comment> rejectComment(@PathVariable UUID id) {
        Comment comment = commentService.reject(id);
        return ApiResponse.success("评论已拒绝", comment);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable UUID id) {
        commentService.delete(id);
        return ApiResponse.success("评论删除成功", null);
    }
}
