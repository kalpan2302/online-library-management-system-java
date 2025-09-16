//package com.olms.backend.controller;
//
//import com.olms.backend.dto.BookRequest;
//import com.olms.backend.dto.IssueRequest;
//import com.olms.backend.dto.ReturnRequest;
//import com.olms.backend.dto.UpdateBookRequest;
//import com.olms.backend.entities.Book;
//import com.olms.backend.entities.Loan;
//import com.olms.backend.service.BookService;
//import com.olms.backend.security.JwtUtil;
//import java.util.List;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/books")
//@PreAuthorize("hasAuthority('ADMIN')") // extra safety
//public class BookController {
//
//    @Autowired
//    private BookService bookService;
//
//    @PostMapping("/add")
//    public ResponseEntity<Book> addBook(@RequestBody BookRequest req) {
//        Book savedBook = bookService.addBook(req);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<Book> updateBook(@PathVariable Integer id,
//                                           @RequestBody UpdateBookRequest req) {
//        return ResponseEntity.ok(bookService.updateBook(id, req));
//    }
//
//    // @DeleteMapping("/delete/{id}")
//    // public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
//    //     bookService.deleteBook(id);
//    //     return ResponseEntity.ok("Book soft-deleted");
//    // }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
//        bookService.deleteBook(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<Book>> getAllBooks() {
//        return ResponseEntity.ok(bookService.getAllBooks());
//    }
//
//
//  @Autowired
//  private JwtUtil jwtUtil;
//
//  @PostMapping("/issue")
//  public ResponseEntity<?> adminIssue(@RequestBody IssueRequest req, @RequestHeader("Authorization") String authHeader) {
//    String token = authHeader.substring(7);
//    String role = jwtUtil.getRole(token);
//    if (!"ADMIN".equals(role)) return ResponseEntity.status(403).body("Only admin allowed");
//
//    // admin may pass userId in req.userId
//    Loan loan = bookService.issueBook(req.getUserId(), req.getBookId(), req.getDueDate(), null, true);
//    return ResponseEntity.status(201).body(loan);
//  }
//
//  @PostMapping("/return")
//  public ResponseEntity<?> adminReturn(@RequestBody ReturnRequest req, @RequestHeader("Authorization") String authHeader) {
//    String token = authHeader.substring(7);
//    String role = jwtUtil.getRole(token);
//    if (!"ADMIN".equals(role)) return ResponseEntity.status(403).body("Only admin allowed");
//
//    Loan loan = bookService.returnBook(req.getLoanId(), req.getBookId(), null, true);
//    return ResponseEntity.ok(loan);
//  }
//
//
//}

// Abhishek

package com.olms.backend.controller;

import com.olms.backend.dto.BookRequest;
import com.olms.backend.dto.IssueRequest;
import com.olms.backend.dto.ReturnRequest;
import com.olms.backend.dto.UpdateBookRequest;
import com.olms.backend.entities.Book;
import com.olms.backend.entities.Loan;
import com.olms.backend.entities.User;
import com.olms.backend.exception.ResourceNotFoundException;
import com.olms.backend.repository.LoanRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.JwtUtil;
import com.olms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
@PreAuthorize("hasRole('ADMIN')")

@Validated
public class BookController {

  @Autowired
  private BookService bookService;
  @Autowired
  private JwtUtil jwtUtil; // keep if you log issuer or need admin id

  @Autowired
  private UserRepository userRepo; // inject userRepo

  @Autowired
  private LoanRepository loanRepo; // inject loanRepo

  @PostMapping("/add")
  public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest req) {
    Book savedBook = bookService.addBook(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
  }

  // @PutMapping("/update/{id}")
  // public ResponseEntity<Book> updateBook(@PathVariable Integer id,
  // @Valid @RequestBody UpdateBookRequest req) {
  // return ResponseEntity.ok(bookService.updateBook(id, req));
  // }

  @PutMapping("/update/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable Integer id,
      @Valid @RequestBody UpdateBookRequest req) {
    return ResponseEntity.ok(bookService.updateBook(id, req));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/all")
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAllBooks());
  }

  @PostMapping("/issue")
  public ResponseEntity<Loan> adminIssue(@Valid @RequestBody IssueRequest req) {
    // ADMIN may issue on behalf of any user; service should validate user & book
    Loan loan = bookService.issueBook(req.getUserId(), req.getBookId(), req.getDueDate(), null, true);
    return ResponseEntity.status(HttpStatus.CREATED).body(loan);
  }

  // @PostMapping("/return")
  // public ResponseEntity<Loan> adminReturn(@Valid @RequestBody ReturnRequest
  // req) {
  // Loan loan = bookService.returnBook(req.getLoanId(), req.getBookId(), null,
  // true);
  // return ResponseEntity.ok(loan);
  // }

  // snigdha
  @PostMapping("/return")
  public ResponseEntity<Loan> adminReturn(@Valid @RequestBody ReturnRequest req) {
    Loan loan = bookService.returnBook(
        req.getUserId(),
        req.getBookId(),
        req.getReturnDate());
    return ResponseEntity.ok(loan);
  }

  @GetMapping("/issued/{userId}")
  public ResponseEntity<List<Book>> getIssuedBooksForUser(@PathVariable Long userId) {
    return ResponseEntity.ok(bookService.getIssuedBooksForUser(userId));
  }

  // All loans
  @GetMapping("/loans")
  public ResponseEntity<List<Loan>> getAllLoans() {
    List<Loan> loans = bookService.getAllLoans();
    return ResponseEntity.ok(loans);
  }

  // Loans for specific user
  @GetMapping("/loans/user")
  public ResponseEntity<List<Loan>> getAllLoansForUser(@RequestParam Long userId) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    List<Loan> loans = loanRepo.findByUser(user);
    return ResponseEntity.ok(loans);
  }
}
