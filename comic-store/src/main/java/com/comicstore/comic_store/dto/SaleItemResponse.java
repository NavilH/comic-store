package com.comicstore.comic_store.dto;

import java.math.BigDecimal;

public record SaleItemResponse(Long id, ComicResponse comic, Integer quantity, BigDecimal unitPrice) {}
