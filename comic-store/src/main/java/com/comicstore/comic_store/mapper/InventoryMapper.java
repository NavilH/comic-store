package com.comicstore.comic_store.mapper;

import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryMapper {

    private final ComicMapper comicMapper;

    public InventoryResponse toResponse(Inventory inv) {
        return new InventoryResponse(inv.getId(), comicMapper.toResponse(inv.getComic()),
                inv.getQuantity(), inv.getLastRestocked());
    }
}
