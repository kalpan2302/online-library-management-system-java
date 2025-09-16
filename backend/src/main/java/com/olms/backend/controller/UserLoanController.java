package com.olms.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olms.backend.entities.Loan;
import com.olms.backend.security.JwtUtil;
import com.olms.backend.service.BookService;

@RestController
@RequestMapping("/api/user/loans")
public class UserLoanController {

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAllLoans(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);
        return ResponseEntity.ok(bookService.getAllLoansForUser(email));
    }

    @GetMapping("/issued")
    public ResponseEntity<List<Loan>> getIssuedBooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);
        return ResponseEntity.ok(bookService.getIssuedBooksForUser(email));
    }

    @GetMapping("/returned")
    public ResponseEntity<List<Loan>> getReturnedBooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);
        return ResponseEntity.ok(bookService.getReturnedBooksForUser(email));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueBooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);
        return ResponseEntity.ok(bookService.getOverdueBooksForUser(email));
    }
}
