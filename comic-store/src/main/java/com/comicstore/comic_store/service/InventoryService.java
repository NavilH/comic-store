package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.InventoryRequest;
import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.entity.Comic;
import com.comicstore.comic_store.entity.Inventory;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.mapper.InventoryMapper;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ComicRepository comicRepository;
    private final InventoryMapper inventoryMapper;

    public List<InventoryResponse> findAll() {
        return inventoryRepository.findAll().stream().map(inventoryMapper::toResponse).toList();
    }

    public InventoryResponse findByComicId(Long comicId) {
        return inventoryMapper.toResponse(inventoryRepository.findByComicId(comicId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for comic: " + comicId)));
    }

    public InventoryResponse create(InventoryRequest request) {
        Comic comic = comicRepository.findById(request.comicId())
                .orElseThrow(() -> new ResourceNotFoundException("Comic not found: " + request.comicId()));
        if (inventoryRepository.findByComicId(request.comicId()).isPresent())
            throw new IllegalStateException("Inventory already exists for comic: " + request.comicId());
        Inventory inventory = Inventory.builder()
                .comic(comic)
                .quantity(request.quantity())
                .lastRestocked(LocalDateTime.now())
                .build();
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse restock(Long id, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setLastRestocked(LocalDateTime.now());
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    public void reduceStock(Long comicId, int quantity) {
        Inventory inventory = inventoryRepository.findByComicId(comicId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for comic: " + comicId));
        if (inventory.getQuantity() < quantity)
            throw new IllegalStateException("Insufficient stock for comic: " + comicId);
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
