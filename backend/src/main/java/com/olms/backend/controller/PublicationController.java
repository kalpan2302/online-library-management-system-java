package com.olms.backend.controller;

import com.olms.backend.dto.PublicationRequest;
import com.olms.backend.dto.UpdatePublicationRequest;
import com.olms.backend.entities.Publication;
import com.olms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/publications")
@PreAuthorize("hasAuthority('ADMIN')")
public class PublicationController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Publication> addPublication(@RequestBody PublicationRequest req) {
        Publication created = bookService.addPublication(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Publication> updatePublication(@PathVariable Integer id,
                                                         @RequestBody UpdatePublicationRequest req) {
        return ResponseEntity.ok(bookService.updatePublication(id, req));
    }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deletePublication(@PathVariable Integer id) {
    //     bookService.deletePublication(id);
    //     return ResponseEntity.ok("Publication soft-deleted");
    // }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable Integer id) {
        bookService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publication>> listPublications() {
        return ResponseEntity.ok(bookService.listPublications());
    }
}