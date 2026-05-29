package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Tag;
import com.pidan.blog.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResponse<List<Tag>> listTags() {
        List<Tag> tags = tagService.listAll();
        return ApiResponse.success(tags);
    }
}
