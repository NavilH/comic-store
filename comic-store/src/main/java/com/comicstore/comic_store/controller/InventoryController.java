package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.dto.InventoryRequest;
import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.dto.RestockRequest;
import com.comicstore.comic_store.service.InventoryService;
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
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    public List<InventoryResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/comic/{comicId}")
    public InventoryResponse findByComicId(@PathVariable Long comicId) {
        return service.findByComicId(comicId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse restock(@PathVariable Long id, @Valid @RequestBody RestockRequest request) {
        return service.restock(id, request.quantity());
    }
}
