package com.comicstore.comic_store.mapper;

import com.comicstore.comic_store.dto.AuthorResponse;
import com.comicstore.comic_store.dto.ComicResponse;
import com.comicstore.comic_store.dto.PublisherResponse;
import com.comicstore.comic_store.entity.Comic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComicMapper {

    public ComicResponse toResponse(Comic c) {
        PublisherResponse publisher = new PublisherResponse(
                c.getPublisher().getId(), c.getPublisher().getName(), c.getPublisher().getCountry());
        List<AuthorResponse> authors = c.getAuthors() == null ? List.of() :
                c.getAuthors().stream().map(a -> new AuthorResponse(a.getId(), a.getName())).toList();
        return new ComicResponse(c.getId(), c.getTitle(), c.getIssueNumber(),
                publisher, authors, c.getGenre(), c.getPrice(), c.getDescription());
    }
}
