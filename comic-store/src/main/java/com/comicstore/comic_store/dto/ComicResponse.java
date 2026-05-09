package com.comicstore.comic_store.dto;

import com.comicstore.comic_store.entity.Genre;

import java.math.BigDecimal;
import java.util.List;

public record ComicResponse(
        Long id,
        String title,
        Integer issueNumber,
        PublisherResponse publisher,
        List<AuthorResponse> authors,
        Genre genre,
        BigDecimal price,
        String description
) {}
