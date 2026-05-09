package com.comicstore.comic_store.dto;

import java.time.LocalDateTime;

public record InventoryResponse(Long id, ComicResponse comic, Integer quantity, LocalDateTime lastRestocked) {}
