package com.comicstore.comic_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleItemRequest(@NotNull Long comicId, @NotNull @Min(1) Integer quantity) {}
