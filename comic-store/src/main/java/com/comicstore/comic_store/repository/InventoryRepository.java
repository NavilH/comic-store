package com.comicstore.comic_store.repository;

import com.comicstore.comic_store.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByComicId(Long comicId);
}
