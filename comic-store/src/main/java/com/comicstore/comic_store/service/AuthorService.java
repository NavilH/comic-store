package com.comicstore.comic_store.service;

import com.comicstore.comic_store.dto.AuthorRequest;
import com.comicstore.comic_store.dto.AuthorResponse;
import com.comicstore.comic_store.entity.Author;
import com.comicstore.comic_store.exception.ResourceNotFoundException;
import com.comicstore.comic_store.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository repository;

    public List<AuthorResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AuthorResponse findById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id)));
    }

    public AuthorResponse create(AuthorRequest request) {
        return toResponse(repository.save(Author.builder().name(request.name()).build()));
    }

    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        author.setName(request.name());
        return toResponse(repository.save(author));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Author not found: " + id);
        repository.deleteById(id);
    }

    AuthorResponse toResponse(Author a) {
        return new AuthorResponse(a.getId(), a.getName());
    }
}
