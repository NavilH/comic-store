package com.comicstore.comic_store.dto;

import jakarta.validation.constraints.NotBlank;

public record PublisherRequest(@NotBlank String name, String country) {}
