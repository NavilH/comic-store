package com.comicstore.comic_store.dto;

import com.comicstore.comic_store.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ComicRequest(
        @NotBlank String title,
        @NotNull Integer issueNumber,
        @NotNull Long publisherId,
        List<Long> authorIds,
        Genre genre,
        @NotNull @Positive BigDecimal price,
        String description
) {}
