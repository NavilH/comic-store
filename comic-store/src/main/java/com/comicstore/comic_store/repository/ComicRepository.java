package com.comicstore.comic_store.repository;

import com.comicstore.comic_store.entity.Comic;
import com.comicstore.comic_store.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComicRepository extends JpaRepository<Comic, Long> {
    List<Comic> findByPublisherId(Long publisherId);
    List<Comic> findByGenre(Genre genre);
}
