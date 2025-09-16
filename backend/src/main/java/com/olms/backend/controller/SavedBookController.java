package com.olms.backend.controller;

import com.olms.backend.dto.SaveBookRequest;
import com.olms.backend.dto.SavedBookResponse;
import com.olms.backend.entities.SavedBook;
import com.olms.backend.service.SavedBookService;
import com.olms.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SavedBookController {

    @Autowired
    private SavedBookService savedBookService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/saved-books")
    public ResponseEntity<?> saveBook(@RequestBody SaveBookRequest req,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token);

            SavedBook savedBook = savedBookService.saveBookForUser(email, req.getBookId());
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/saved-books")
    public ResponseEntity<?> getSavedBooks(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token);

            List<SavedBookResponse> savedBooks = savedBookService.getSavedBooksForUser(email);
            return ResponseEntity.ok(savedBooks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}