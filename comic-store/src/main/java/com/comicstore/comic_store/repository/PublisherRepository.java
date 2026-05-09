package com.comicstore.comic_store.repository;

import com.comicstore.comic_store.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {}
