package com.pidan.blog.repository;

import com.pidan.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByPostIdAndApprovedTrue(UUID postId);

    List<Comment> findByPostId(UUID postId);

    long countByPostIdAndApprovedTrue(UUID postId);

    long countByApprovedFalse();

    Page<Comment> findByPostIdOrderByCreatedAtDesc(UUID postId, Pageable pageable);

    Page<Comment> findByApprovedOrderByCreatedAtDesc(Boolean approved, Pageable pageable);

    Page<Comment> findByApproved(Boolean approved, Pageable pageable);
}
