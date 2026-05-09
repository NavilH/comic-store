package com.comicstore.comic_store.repository;

import com.comicstore.comic_store.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {}
