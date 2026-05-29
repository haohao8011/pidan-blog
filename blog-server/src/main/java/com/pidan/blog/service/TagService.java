package com.pidan.blog.service;

import com.pidan.blog.entity.Tag;
import com.pidan.blog.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> listAll() {
        return tagRepository.findAll();
    }

    public Tag getBySlug(String slug) {
        return tagRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
    }

    public Tag getById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));
    }

    @Transactional
    public Tag create(Tag tag) {
        if (tag.getSlug() == null || tag.getSlug().isBlank()) {
            tag.setSlug(generateSlug(tag.getName()));
        }
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag update(UUID id, Tag updatedTag) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        tag.setName(updatedTag.getName());
        if (updatedTag.getSlug() != null && !updatedTag.getSlug().isBlank()) {
            tag.setSlug(updatedTag.getSlug());
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("标签不存在");
        }
        tagRepository.deleteById(id);
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s-]");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slug.isEmpty()) {
            slug = "tag-" + System.currentTimeMillis();
        }

        String originalSlug = slug;
        int counter = 1;
        while (tagRepository.findBySlug(slug).isPresent()) {
            slug = originalSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
