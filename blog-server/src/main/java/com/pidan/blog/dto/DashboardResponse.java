package com.pidan.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Long totalPosts;
    private Long publishedPosts;
    private Long totalComments;
    private Long pendingComments;
    private Long totalViews;
    private Long totalLikes;
}
