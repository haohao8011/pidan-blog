package com.pidan.blog.common;

import java.text.Normalizer;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class SlugUtils {

    private static final Pattern NON_SLUG_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s-]");
    private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern MULTI_DASH_PATTERN = Pattern.compile("-+");

    private SlugUtils() {}

    public static String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            return "item-" + System.currentTimeMillis();
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String slug = NON_SLUG_PATTERN.matcher(normalized).replaceAll("")
                .toLowerCase()
                .trim();
        slug = MULTI_SPACE_PATTERN.matcher(slug).replaceAll("-");
        slug = MULTI_DASH_PATTERN.matcher(slug).replaceAll("-");

        return slug.isEmpty() ? "item-" + System.currentTimeMillis() : slug;
    }

    public static String ensureUnique(String slug, Function<String, Optional<?>> existsFn) {
        String original = slug;
        int counter = 1;
        while (existsFn.apply(slug).isPresent()) {
            slug = original + "-" + counter;
            counter++;
        }
        return slug;
    }
}
