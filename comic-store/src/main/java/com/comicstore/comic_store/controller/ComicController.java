package com.comicstore.comic_store.controller;

import com.comicstore.comic_store.dto.ComicRequest;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.PageResponse;
import com.comicstore.comic_store.entity.Genre;
import com.comicstore.comic_store.service.ComicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comics")
@RequiredArgsConstructor
public class ComicController {

    private final ComicService service;

    @GetMapping
    public Object findAll(
            @RequestParam(required = false) Long publisherId,
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        if (publisherId != null) return service.findByPublisher(publisherId);
        if (genre != null) return service.findByGenre(genre);
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ComicResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComicResponse> create(@Valid @RequestBody ComicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ComicResponse update(@PathVariable Long id, @Valid @RequestBody ComicRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
