package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.dto.SaleRequest;
import com.comicstore.comic_store.dto.SaleResponse;
import com.comicstore.comic_store.entity.Comic;
import com.comicstore.comic_store.entity.Sale;
import com.comicstore.comic_store.entity.SaleItem;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.mapper.SaleMapper;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ComicRepository comicRepository;
    private final InventoryService inventoryService;
    private final SaleMapper saleMapper;

    public PageResponse<SaleResponse> findAll(Pageable pageable) {
        return PageResponse.of(saleRepository.findAll(pageable).map(saleMapper::toResponse));
    }

    public SaleResponse findById(Long id) {
        return saleMapper.toResponse(saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + id)));
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        List<SaleItem> items = request.items().stream().map(itemRequest -> {
            Comic comic = comicRepository.findById(itemRequest.comicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comic not found: " + itemRequest.comicId()));
            inventoryService.reduceStock(itemRequest.comicId(), itemRequest.quantity());
            return SaleItem.builder()
                    .comic(comic)
                    .quantity(itemRequest.quantity())
                    .unitPrice(comic.getPrice())
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Sale sale = Sale.builder()
                .saleDate(LocalDateTime.now())
                .totalAmount(total)
                .items(items)
                .build();

        items.forEach(i -> i.setSale(sale));

        return saleMapper.toResponse(saleRepository.save(sale));
    }
}
