package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.dto.SaleItemRequest;
import com.comicstore.comic_store.dto.SaleRequest;
import com.comicstore.comic_store.dto.SaleResponse;
import com.comicstore.comic_store.entity.*;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.mapper.ComicMapper;
import com.comicstore.comic_store.mapper.SaleMapper;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock SaleRepository saleRepository;
    @Mock ComicRepository comicRepository;
    @Mock InventoryService inventoryService;
    @Spy  SaleMapper saleMapper = new SaleMapper(new ComicMapper());

    @InjectMocks SaleService service;

    private Comic comic;
    private Sale sale;

    @BeforeEach
    void setUp() {
        Publisher publisher = Publisher.builder().id(1L).name("Marvel").country("US").build();
        comic = Comic.builder().id(1L).title("Spider-Man").issueNumber(1)
                .publisher(publisher).authors(List.of())
                .genre(Genre.SUPERHERO).price(new BigDecimal("3.99")).build();

        SaleItem item = SaleItem.builder()
                .id(1L).comic(comic).quantity(2).unitPrice(new BigDecimal("3.99")).build();
        sale = Sale.builder()
                .id(1L).saleDate(LocalDateTime.now())
                .totalAmount(new BigDecimal("7.98"))
                .items(List.of(item)).build();
    }

    @Test
    void findAll_returnsPagedSales() {
        when(saleRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sale)));

        PageResponse<SaleResponse> result = service.findAll(Pageable.unpaged());

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
    }

    @Test
    void findById_returnsSale() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        SaleResponse result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("7.98"));
    }

    @Test
    void findById_throws_whenNotFound() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_calculatesTotal_andReducesInventory() {
        SaleRequest request = new SaleRequest(List.of(new SaleItemRequest(1L, 2)));
        when(comicRepository.findById(1L)).thenReturn(Optional.of(comic));
        doNothing().when(inventoryService).reduceStock(1L, 2);
        when(saleRepository.save(any())).thenReturn(sale);

        SaleResponse result = service.create(request);

        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("7.98"));
        verify(inventoryService).reduceStock(1L, 2);
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void create_throws_whenComicMissing() {
        SaleRequest request = new SaleRequest(List.of(new SaleItemRequest(99L, 1)));
        when(comicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
