package com.comicstore.comic_store.mapper;

import com.comicstore.comic_store.dto.SaleItemResponse;
import com.comicstore.comic_store.dto.SaleResponse;
import com.comicstore.comic_store.entity.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final ComicMapper comicMapper;

    public SaleResponse toResponse(Sale s) {
        List<SaleItemResponse> items = s.getItems().stream()
                .map(i -> new SaleItemResponse(i.getId(), comicMapper.toResponse(i.getComic()),
                        i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new SaleResponse(s.getId(), s.getSaleDate(), s.getTotalAmount(), items);
    }
}
