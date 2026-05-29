package com.pidan.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private UUID id;
    private String title;
    private String slug;
    private String content;
    private String contentHtml;
    private String coverImage;
    private String categoryName;
    private List<String> tagNames;
    private Long viewCount;
    private Long likeCount;
    private Boolean published;
    private Boolean pinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;
}
