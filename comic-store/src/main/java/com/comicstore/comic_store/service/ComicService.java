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
import com.comicstore.comic_store.repository.InventoryRepository;
import com.comicstore.comic_store.repository.PublisherRepository;
import com.comicstore.comic_store.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComicService {

    private final ComicRepository comicRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final InventoryRepository inventoryRepository;
    private final SaleItemRepository saleItemRepository;
    private final ComicMapper comicMapper;

    public PageResponse<ComicResponse> findAll(Pageable pageable) {
        return PageResponse.of(comicRepository.findAll(pageable).map(comicMapper::toResponse));
    }

    public ComicResponse findById(Long id) {
        return comicMapper.toResponse(comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic not found: " + id)));
    }

    public List<ComicResponse> findByPublisher(Long publisherId) {
        return comicRepository.findByPublisherId(publisherId).stream().map(comicMapper::toResponse).toList();
    }

    public List<ComicResponse> findByGenre(Genre genre) {
        return comicRepository.findByGenre(genre).stream().map(comicMapper::toResponse).toList();
    }

    public ComicResponse create(ComicRequest request) {
        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + request.publisherId()));
        Comic comic = Comic.builder()
                .title(request.title())
                .issueNumber(request.issueNumber())
                .publisher(publisher)
                .authors(resolveAuthors(request.authorIds()))
                .genre(request.genre())
                .price(request.price())
                .description(request.description())
                .build();
        return comicMapper.toResponse(comicRepository.save(comic));
    }

    public ComicResponse update(Long id, ComicRequest request) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic not found: " + id));
        Publisher publisher = publisherRepository.findById(request.publisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + request.publisherId()));
        comic.setTitle(request.title());
        comic.setIssueNumber(request.issueNumber());
        comic.setPublisher(publisher);
        comic.setAuthors(resolveAuthors(request.authorIds()));
        comic.setGenre(request.genre());
        comic.setPrice(request.price());
        comic.setDescription(request.description());
        return comicMapper.toResponse(comicRepository.save(comic));
    }

    @Transactional
    public void delete(Long id) {
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comic not found: " + id));
        if (saleItemRepository.existsByComicId(id))
            throw new IllegalStateException("Cannot delete comic that has been sold");
        inventoryRepository.deleteByComicId(id);
        comic.setAuthors(java.util.List.of());
        comicRepository.save(comic);
        comicRepository.deleteById(id);
    }

    private List<Author> resolveAuthors(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return authorRepository.findAllById(ids);
    }
}
