package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.PublisherRequest;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.entity.Publisher;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.repository.ComicRepository;
import com.comicstore.comic_store.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository repository;
    private final ComicRepository comicRepository;

    public List<PublisherResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public PublisherResponse findById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + id)));
    }

    public PublisherResponse create(PublisherRequest request) {
        Publisher publisher = Publisher.builder()
                .name(request.name())
                .country(request.country())
                .build();
        return toResponse(repository.save(publisher));
    }

    public PublisherResponse update(Long id, PublisherRequest request) {
        Publisher publisher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + id));
        publisher.setName(request.name());
        publisher.setCountry(request.country());
        return toResponse(repository.save(publisher));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Publisher not found: " + id);
        if (comicRepository.existsByPublisherId(id))
            throw new IllegalStateException("Cannot delete publisher with existing comics");
        repository.deleteById(id);
    }

    PublisherResponse toResponse(Publisher p) {
        return new PublisherResponse(p.getId(), p.getName(), p.getCountry());
    }
}
