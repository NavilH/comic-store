package com.comicstore.comic_store.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SaleRequest(@NotEmpty List<SaleItemRequest> items) {}
