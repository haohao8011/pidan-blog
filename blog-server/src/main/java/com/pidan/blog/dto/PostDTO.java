package com.pidan.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String slug;

    private String content;

    private UUID categoryId;

    private String coverImage;

    private List<String> tagNames;

    private Boolean published;

    private Boolean pinned;
}
