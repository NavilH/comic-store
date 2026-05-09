package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.InventoryRequest;
import com.comicstore.comic_store.dto.InventoryResponse;
import com.comicstore.comic_store.entity.Comic;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.entity.Inventory;
import com.comicstore.comic_store.entity.Publisher;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.mapper.ComicMapper;
import com.comicstore.comic_store.mapper.InventoryMapper;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock InventoryRepository inventoryRepository;
    @Mock ComicRepository comicRepository;
    @Spy  InventoryMapper inventoryMapper = new InventoryMapper(new ComicMapper());

    @InjectMocks InventoryService service;

    private Comic comic;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        Publisher publisher = Publisher.builder().id(1L).name("Marvel").country("US").build();
        comic = Comic.builder().id(1L).title("Spider-Man").issueNumber(1)
                .publisher(publisher).authors(List.of())
                .genre(Genre.SUPERHERO).price(new BigDecimal("3.99")).build();
        inventory = Inventory.builder().id(1L).comic(comic).quantity(10)
                .lastRestocked(LocalDateTime.now()).build();
    }

    @Test
    void findAll_returnsAll() {
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        List<InventoryResponse> result = service.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByComicId_returnsInventory() {
        when(inventoryRepository.findByComicId(1L)).thenReturn(Optional.of(inventory));

        InventoryResponse result = service.findByComicId(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(10);
    }

    @Test
    void findByComicId_throws_whenMissing() {
        when(inventoryRepository.findByComicId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByComicId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesInventory() {
        InventoryRequest request = new InventoryRequest(1L, 10);
        when(comicRepository.findById(1L)).thenReturn(Optional.of(comic));
        when(inventoryRepository.findByComicId(1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any())).thenReturn(inventory);

        InventoryResponse result = service.create(request);

        assertThat(result.quantity()).isEqualTo(10);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void create_throws_whenDuplicate() {
        InventoryRequest request = new InventoryRequest(1L, 5);
        when(comicRepository.findById(1L)).thenReturn(Optional.of(comic));
        when(inventoryRepository.findByComicId(1L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void restock_addsQuantityAndSaves() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);

        service.restock(1L, 5);

        assertThat(inventory.getQuantity()).isEqualTo(15);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void reduceStock_reducesQuantityAndSaves() {
        when(inventoryRepository.findByComicId(1L)).thenReturn(Optional.of(inventory));

        service.reduceStock(1L, 3);

        assertThat(inventory.getQuantity()).isEqualTo(7);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void reduceStock_throws_whenInsufficient() {
        when(inventoryRepository.findByComicId(1L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> service.reduceStock(1L, 20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient");
    }
}
