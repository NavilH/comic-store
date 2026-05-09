package com.comicstore.comic_store.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(@NotBlank String name) {}
