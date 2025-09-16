package com.olms.backend.repository;

import com.olms.backend.entities.SavedBook;
import com.olms.backend.entities.User;
import com.olms.backend.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedBookRepository extends JpaRepository<SavedBook, Long> {
    boolean existsByUserAndBook(User user, Book book);
    List<SavedBook> findAllByUser(User user);
}