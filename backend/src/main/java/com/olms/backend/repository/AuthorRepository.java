package com.olms.backend.repository;

import com.olms.backend.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findByNameIgnoreCase(String name);
}