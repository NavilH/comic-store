package com.comicstore.comic_store.repository;

import com.comicstore.comic_store.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    boolean existsByComicId(Long comicId);
}
