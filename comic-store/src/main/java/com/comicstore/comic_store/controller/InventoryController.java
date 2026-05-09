package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.dto.InventoryRequest;
import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.dto.RestockRequest;
import com.comicstore.comic_store.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Track stock levels for comics")
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    @Operation(summary = "List all inventory records")
    public List<InventoryResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/comic/{comicId}")
    @Operation(summary = "Get inventory record for a specific comic")
    public InventoryResponse findByComicId(@PathVariable Long comicId) {
        return service.findByComicId(comicId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an inventory record for a comic")
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add stock to an inventory record")
    public InventoryResponse restock(@PathVariable Long id, @Valid @RequestBody RestockRequest request) {
        return service.restock(id, request.quantity());
    }
}
