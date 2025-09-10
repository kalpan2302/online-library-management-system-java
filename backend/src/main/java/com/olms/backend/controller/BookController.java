package com.olms.backend.controller;

import com.olms.backend.dto.BookRequest;
import com.olms.backend.dto.UpdateBookRequest;
import com.olms.backend.entities.Book;
import com.olms.backend.service.BookService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/books")
@PreAuthorize("hasAuthority('ADMIN')") // extra safety
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Book> addBook(@RequestBody BookRequest req) {
        Book savedBook = bookService.addBook(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id,
                                           @RequestBody UpdateBookRequest req) {
        return ResponseEntity.ok(bookService.updateBook(id, req));
    }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
    //     bookService.deleteBook(id);
    //     return ResponseEntity.ok("Book soft-deleted");
    // }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
}