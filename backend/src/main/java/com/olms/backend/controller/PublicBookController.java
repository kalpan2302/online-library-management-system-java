package com.olms.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.olms.backend.entities.Book;
import com.olms.backend.service.BookService;

@RestController
@RequestMapping("/api/books")
public class PublicBookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllActiveBooks() {
        List<Book> activeBooks = bookService.getAllActiveBooks();
        return ResponseEntity.ok(activeBooks);
    }
}

