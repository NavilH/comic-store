package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.ComicRequest;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.entity.Author;
import com.comicstore.comic_store.entity.Comic;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.entity.Publisher;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.mapper.ComicMapper;
import com.comicstore.comic_store.repository.AuthorRepository;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.PublisherRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComicServiceTest {

    @Mock ComicRepository comicRepository;
    @Mock PublisherRepository publisherRepository;
    @Mock AuthorRepository authorRepository;
    @Spy  ComicMapper comicMapper = new ComicMapper();

    @InjectMocks ComicService service;

    private Publisher publisher;
    private Author author;
    private Comic comic;

    @BeforeEach
    void setUp() {
        publisher = Publisher.builder().id(1L).name("Marvel").country("US").build();
        author = Author.builder().id(1L).name("Stan Lee").build();
        comic = Comic.builder()
                .id(1L).title("Spider-Man").issueNumber(1)
                .publisher(publisher).authors(List.of(author))
                .genre(Genre.SUPERHERO).price(new BigDecimal("3.99"))
                .description("Classic").build();
    }

    @Test
    void findAll_returnsPagedComics() {
        when(comicRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comic)));

        PageResponse<ComicResponse> result = service.findAll(Pageable.unpaged());

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("Spider-Man");
        assertThat(result.totalElements()).isEqualTo(1L);
    }

    @Test
    void findById_returnsComic() {
        when(comicRepository.findById(1L)).thenReturn(Optional.of(comic));

        ComicResponse result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Spider-Man");
    }

    @Test
    void findById_throws_whenNotFound() {
        when(comicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByPublisher_returnsFiltered() {
        when(comicRepository.findByPublisherId(1L)).thenReturn(List.of(comic));

        assertThat(service.findByPublisher(1L)).hasSize(1);
    }

    @Test
    void findByGenre_returnsFiltered() {
        when(comicRepository.findByGenre(Genre.SUPERHERO)).thenReturn(List.of(comic));

        List<ComicResponse> result = service.findByGenre(Genre.SUPERHERO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).genre()).isEqualTo(Genre.SUPERHERO);
    }

    @Test
    void create_savesAndReturnsComic() {
        ComicRequest request = new ComicRequest(
                "Spider-Man", 1, 1L, List.of(1L), Genre.SUPERHERO, new BigDecimal("3.99"), "Classic");
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(authorRepository.findAllById(List.of(1L))).thenReturn(List.of(author));
        when(comicRepository.save(any())).thenReturn(comic);

        ComicResponse result = service.create(request);

        assertThat(result.title()).isEqualTo("Spider-Man");
        verify(comicRepository).save(any(Comic.class));
    }

    @Test
    void create_throws_whenPublisherMissing() {
        ComicRequest request = new ComicRequest(
                "X", 1, 99L, null, Genre.ACTION, new BigDecimal("1.00"), null);
        when(publisherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_updatesComic() {
        ComicRequest request = new ComicRequest(
                "Updated", 2, 1L, List.of(), Genre.ACTION, new BigDecimal("4.99"), null);
        when(comicRepository.findById(1L)).thenReturn(Optional.of(comic));
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(comicRepository.save(any())).thenReturn(comic);

        service.update(1L, request);

        verify(comicRepository).save(comic);
    }

    @Test
    void update_throws_whenComicMissing() {
        ComicRequest request = new ComicRequest(
                "X", 1, 1L, null, Genre.ACTION, new BigDecimal("1.00"), null);
        when(comicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesComic() {
        when(comicRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(comicRepository).deleteById(1L);
    }

    @Test
    void delete_throws_whenNotFound() {
        when(comicRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
