package com.pidan.blog.service;

import com.pidan.blog.dto.DashboardResponse;
import com.pidan.blog.entity.Post;
import com.pidan.blog.repository.CommentRepository;
import com.pidan.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public DashboardService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public DashboardResponse getDashboard() {
        List<Post> allPosts = postRepository.findAll();
        long totalPosts = allPosts.size();
        long publishedPosts = allPosts.stream().filter(Post::getPublished).count();
        long totalViews = allPosts.stream().mapToLong(Post::getViewCount).sum();
        long totalLikes = allPosts.stream().mapToLong(Post::getLikeCount).sum();
        long totalComments = commentRepository.count();
        long pendingComments = commentRepository.countByApprovedFalse();

        return DashboardResponse.builder()
                .totalPosts(totalPosts)
                .publishedPosts(publishedPosts)
                .totalComments(totalComments)
                .pendingComments(pendingComments)
                .totalViews(totalViews)
                .totalLikes(totalLikes)
                .build();
    }
}
