package com.pidan.blog.service;

import com.pidan.blog.dto.CommentDTO;
import com.pidan.blog.entity.Comment;
import com.pidan.blog.entity.Post;
import com.pidan.blog.repository.CommentRepository;
import com.pidan.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> listByPost(UUID postId, boolean approvedOnly) {
        if (approvedOnly) {
            return commentRepository.findByPostIdAndApprovedTrue(postId);
        }
        return commentRepository.findByPostId(postId);
    }

    public Page<Comment> listAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public Page<Comment> listAllByApproved(Boolean approved, Pageable pageable) {
        if (approved != null) {
            return commentRepository.findByApproved(approved, pageable);
        }
        return commentRepository.findAll(pageable);
    }

    @Transactional
    public Comment create(CommentDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        Comment comment = Comment.builder()
                .post(post)
                .parentId(dto.getParentId())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .content(dto.getContent())
                .approved(false)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment approve(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        comment.setApproved(true);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment reject(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        comment.setApproved(false);
        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("评论不存在");
        }
        commentRepository.deleteById(id);
    }

    public long getPendingCount() {
        return commentRepository.countByApprovedFalse();
    }

    public long count() {
        return commentRepository.count();
    }
}
