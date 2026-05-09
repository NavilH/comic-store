package com.comicstore.comic_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(@NotNull Long comicId, @NotNull @Min(0) Integer quantity) {}
