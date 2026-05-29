package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.Media;
import com.pidan.blog.service.MediaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    private final MediaService mediaService;

    public UploadController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public ApiResponse<Media> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Media media = mediaService.upload(file);
        return ApiResponse.success("文件上传成功", media);
    }

    @GetMapping
    public ApiResponse<List<Media>> listMedia() {
        List<Media> mediaList = mediaService.listAll();
        return ApiResponse.success(mediaList);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMedia(@PathVariable UUID id) {
        mediaService.delete(id);
        return ApiResponse.success("文件删除成功", null);
    }
}
