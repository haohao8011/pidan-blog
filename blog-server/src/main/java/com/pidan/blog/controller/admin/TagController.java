package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Tag;
import com.pidan.blog.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tags")
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

    @GetMapping("/{id}")
    public ApiResponse<Tag> getTag(@PathVariable UUID id) {
        Tag tag = tagService.getById(id);
        return ApiResponse.success(tag);
    }

    @PostMapping
    public ApiResponse<Tag> createTag(@RequestBody Tag tag) {
        Tag created = tagService.create(tag);
        return ApiResponse.success("标签创建成功", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Tag> updateTag(@PathVariable UUID id,
                                       @RequestBody Tag tag) {
        Tag updated = tagService.update(id, tag);
        return ApiResponse.success("标签更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable UUID id) {
        tagService.delete(id);
        return ApiResponse.success("标签删除成功", null);
    }
}
