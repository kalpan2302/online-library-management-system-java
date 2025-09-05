package com.olms.backend.controller;

import com.olms.backend.dto.AuthorRequest;
import com.olms.backend.dto.UpdateAuthorRequest;
import com.olms.backend.entities.Author;
import com.olms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/authors")
@PreAuthorize("hasAuthority('ADMIN')")
public class AuthorController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Author> addAuthor(@RequestBody AuthorRequest req) {
        Author created = bookService.addAuthor(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Integer id,
            @RequestBody UpdateAuthorRequest req) {
        return ResponseEntity.ok(bookService.updateAuthor(id, req));
    }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deleteAuthor(@PathVariable Integer id) {
    // bookService.deleteAuthor(id);
    // return ResponseEntity.ok("Author soft-deleted");
    // }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Integer id) {
        bookService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Author>> listAuthors() {
        return ResponseEntity.ok(bookService.listAuthors());
    }
}