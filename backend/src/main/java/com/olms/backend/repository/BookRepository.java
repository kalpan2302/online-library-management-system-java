package com.olms.backend.repository;

import com.olms.backend.entities.Book;
import com.olms.backend.entities.Author;
import com.olms.backend.entities.Category;
import com.olms.backend.entities.Publication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// public interface BookRepository extends JpaRepository<Book, Integer> {
//     Optional<Book> findByIsbn(String isbn);
// }

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAuthors(Author author);
    List<Book> findByCategory(Category category);
    List<Book> findByPublication(Publication publication);
}