package com.pidan.blog.service;

import com.pidan.blog.entity.Media;
import com.pidan.blog.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    @Value("${blog.upload.path:./uploads}")
    private String uploadPath;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public Media upload(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        // 生成文件名: yyyyMMdd/uuid_originalFilename
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path dateDirPath = uploadDir.resolve(dateDir);
        Files.createDirectories(dateDirPath);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        Path filePath = dateDirPath.resolve(filename);
        file.transferTo(filePath.toFile());

        String url = "/uploads/" + dateDir + "/" + filename;

        Media media = Media.builder()
                .filename(filename)
                .originalName(originalFilename)
                .url(url)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .build();

        return mediaRepository.save(media);
    }

    public List<Media> listAll() {
        return mediaRepository.findAll();
    }

    @Transactional
    public void delete(UUID id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文件不存在"));

        // 删除物理文件
        try {
            Path filePath = Paths.get(uploadPath).resolve(media.getUrl().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 文件可能已不存在，忽略
        }

        mediaRepository.deleteById(id);
    }
}
