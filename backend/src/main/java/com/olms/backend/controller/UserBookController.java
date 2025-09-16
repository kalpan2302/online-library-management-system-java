////package com.olms.backend.controller;
////
////import com.olms.backend.dto.IssueRequest;
////import com.olms.backend.dto.ReturnRequest;
////import com.olms.backend.entities.Loan;
////import com.olms.backend.service.BookService;
////import com.olms.backend.security.JwtUtil;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////@RestController
////@RequestMapping("/api/books")
////public class UserBookController {
////
////  @Autowired
////  private BookService bookService;
////
////  @Autowired
////  private JwtUtil jwtUtil;
////
////  @PostMapping("/issue")
////  public ResponseEntity<?> issueBook(@RequestBody IssueRequest req, @RequestHeader("Authorization") String authHeader) {
////    String token = authHeader.substring(7);
////    String email = jwtUtil.getEmail(token);
////    Loan loan = bookService.issueBook(null, req.getBookId(), req.getDueDate(), email, false);
////    return ResponseEntity.ok(loan);
////  }
////
////  @PostMapping("/return")
////  public ResponseEntity<?> returnBook(@RequestBody ReturnRequest req, @RequestHeader("Authorization") String authHeader) {
////    String token = authHeader.substring(7);
////    String email = jwtUtil.getEmail(token);
////    Loan loan = bookService.returnBook(req.getLoanId(), req.getBookId(), email, false);
////    return ResponseEntity.ok(loan);
////  }
////}
//
//// Abhishek
//package com.olms.backend.controller;
//
//import com.olms.backend.dto.IssueRequest;
//import com.olms.backend.dto.ReturnRequest;
//import com.olms.backend.entities.Loan;
//import com.olms.backend.security.JwtUtil;
//import com.olms.backend.service.BookService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/books")
//@Validated
//public class UserBookController {
//
//  @Autowired private BookService bookService;
//  @Autowired private JwtUtil jwtUtil;
//
////  @PostMapping("/issue")
////  @PreAuthorize("hasAnyRole('USER','ADMIN')")
////  public ResponseEntity<Loan> issueBook(@Valid @RequestBody IssueRequest req,
////                                        @RequestHeader("Authorization") String authHeader) {
////    String token = authHeader.substring(7);
////    Long uid = jwtUtil.getUserId(token);
////    // enforce self-scope: always use the authenticated user's id
////    Loan loan = bookService.issueBook(uid, req.getBookId(), req.getDueDate(), null, false);
////    return ResponseEntity.ok(loan);
////  }
////
////  @PostMapping("/return")
////  @PreAuthorize("hasAnyRole('USER','ADMIN')")
////  public ResponseEntity<Loan> returnBook(@Valid @RequestBody ReturnRequest req,
////                                         @RequestHeader("Authorization") String authHeader) {
////    String token = authHeader.substring(7);
////    Long uid = jwtUtil.getUserId(token);
////    Loan loan = bookService.returnBook(req.getLoanId(), req.getBookId(), null, false);
////    // service should verify that this loan belongs to uid (defense-in-depth)
////    return ResponseEntity.ok(loan);
////  }
//
//  // Abhishek 2nd attempt
//
//  @PostMapping("/issue")
//  public ResponseEntity<?> issueBook(@RequestBody IssueRequest req) {
//    String email = com.olms.backend.security.SecurityUtil.currentEmail();
//    var loan = bookService.issueBook(null, req.getBookId(), req.getDueDate(), email, false);
//    return ResponseEntity.ok(loan);
//  }
//
//  @PostMapping("/return")
//  public ResponseEntity<?> returnBook(@RequestBody ReturnRequest req) {
//    String email = com.olms.backend.security.SecurityUtil.currentEmail();
//    var loan = bookService.returnBook(req.getLoanId(), req.getBookId(), email, false);
//    return ResponseEntity.ok(loan);
//  }
//
//}

// com/olms/backend/controller/UserBookController.java
package com.olms.backend.controller;

import com.olms.backend.dto.IssueRequest;
import com.olms.backend.dto.ReturnRequest;
import com.olms.backend.entities.Loan;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
public class UserBookController {

  @Autowired
  private BookService bookService;

  @PostMapping("/issue")
  public ResponseEntity<Loan> issueBook(@RequestBody IssueRequest req) {
    String email = SecurityUtil.currentEmail();
    var loan = bookService.issueBook(null, req.getBookId(), req.getDueDate(), email, false);
    return ResponseEntity.ok(loan);
  }

  // @PostMapping("/return")
  // public ResponseEntity<Loan> returnBook(@RequestBody ReturnRequest req) {
  // String email = SecurityUtil.currentEmail();
  // var loan = bookService.returnBook(req.getLoanId(), req.getBookId(), email,
  // false);
  // return ResponseEntity.ok(loan);
  // }

  // snigdha
  @PostMapping("/return")
  public ResponseEntity<Loan> returnBook(@RequestBody ReturnRequest req) {
    Loan loan = bookService.returnBook(
        req.getUserId(),
        req.getBookId(),
        req.getReturnDate());
    return ResponseEntity.ok(loan);
  }

}
