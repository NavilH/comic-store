package com.comicstore.comic_store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(Long id, LocalDateTime saleDate, BigDecimal totalAmount, List<SaleItemResponse> items) {}
