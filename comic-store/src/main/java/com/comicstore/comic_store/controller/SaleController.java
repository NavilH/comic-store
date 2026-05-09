package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.dto.SaleRequest;
import com.comicstore.comic_store.dto.SaleResponse;
import com.comicstore.comic_store.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Record and review sales transactions")
public class SaleController {

    private final SaleService service;

    @GetMapping
    @Operation(summary = "List all sales (paginated)")
    public PageResponse<SaleResponse> findAll(
            @PageableDefault(size = 20, sort = "saleDate") Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a sale by ID")
    public SaleResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record a new sale")
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}
