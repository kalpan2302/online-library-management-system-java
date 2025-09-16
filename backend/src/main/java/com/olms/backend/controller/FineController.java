//package com.olms.backend.controller;
//
//import com.olms.backend.dto.FinePaymentRequest;
//import com.olms.backend.entities.Fine;
//import com.olms.backend.entities.FinePayment;
//import com.olms.backend.entities.User;
//import com.olms.backend.service.FineService;
//import com.olms.backend.repository.UserRepository;
//import com.olms.backend.security.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.GrantedAuthority;
//import com.olms.backend.repository.FineRepository; // if not present
//import com.olms.backend.exception.ResourceNotFoundException;
//
//@RestController
//@RequestMapping("/api/fines")
//public class FineController {
//
//  @Autowired
//  private FineService fineService;
//
//  @Autowired
//  private JwtUtil jwtUtil;
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Autowired
//  private FineRepository fineRepository;
//  // List fines for current user
//  @GetMapping("/my")
//  public ResponseEntity<?> getMyFines(@RequestHeader("Authorization") String authHeader) {
//    String token = authHeader.substring(7);
//    String email = jwtUtil.getEmail(token);
//    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
//    List<Fine> fines = fineService.getFinesForUser(user.getId());
//    return ResponseEntity.ok(fines);
//  }
//
//  @PostMapping("/pay")
//  public ResponseEntity<?> payFine(@RequestBody FinePaymentRequest req, @RequestHeader("Authorization") String authHeader) {
//    String token = authHeader.substring(7);
//    String email = jwtUtil.getEmail(token);
//    User caller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
//
//    Fine fine = fineRepository.findById(req.getFineId())
//      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
//
//    // check permission: owner or admin
//    boolean isOwner = caller.getId().equals(fine.getUser().getId());
//    boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//      .stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ADMIN"));
//
//    if (!isOwner && !isAdmin) {
//      return ResponseEntity.status(403).body("Only fine owner or admin can pay this fine");
//    }
//
//    // proceed with payment via service
//    FinePayment payment = fineService.payFine(req.getFineId(), caller.getId(), req.getAmountPaid(),
//      req.getPaymentMethod(), req.getTransactionRef(), req.getNote());
//    return ResponseEntity.ok(payment);
//  }
//}

// Abhishek

//package com.olms.backend.controller;
//
//import com.olms.backend.dto.FinePaymentRequest;
//import com.olms.backend.entities.Fine;
//import com.olms.backend.entities.FinePayment;
//import com.olms.backend.entities.User;
//import com.olms.backend.exception.ResourceNotFoundException;
//import com.olms.backend.repository.FineRepository;
//import com.olms.backend.repository.UserRepository;
//import com.olms.backend.security.JwtUtil;
//import com.olms.backend.service.FineService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/fines")
//@Validated
//public class FineController {
//
//  @Autowired private FineService fineService;
//  @Autowired private JwtUtil jwtUtil;
//  @Autowired private UserRepository userRepository;
//  @Autowired private FineRepository fineRepository;
//
////  @GetMapping("/my")
////  public ResponseEntity<List<Fine>> getMyFines(@RequestHeader("Authorization") String authHeader) {
////    String token = authHeader.substring(7);
////    Long uid = jwtUtil.getUserId(token);
////    if (uid == null) {
////      // Fallback to email if needed
////      String email = jwtUtil.getEmail(token);
////      User user = userRepository.findByEmailIgnoreCase(email)
////        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
////      uid = user.getId();
////    }
////    return ResponseEntity.ok(fineService.getFinesForUser(uid));
////  }
//
//  // ABhishek
//
////  @GetMapping("/my")
////  public ResponseEntity<?> getMyFines(@RequestHeader("Authorization") String authHeader) {
////    if (authHeader == null || !authHeader.startsWith("Bearer "))
////      return ResponseEntity.status(401).body("Missing Authorization header");
////    String email = jwtUtil.getEmail(authHeader.substring(7));
////    var user = userRepository.findByEmailIgnoreCase(email.trim())
////      .orElseThrow(() -> new RuntimeException("User not found"));
////    return ResponseEntity.ok(fineService.getFinesForUser(user.getId()));
////  }
//
//  // Abhishek 2nd attempt
//
//
//  @GetMapping("/my")
//  public ResponseEntity<?> getMyFines() {
//    String email = com.olms.backend.security.SecurityUtil.currentEmail();
//    var user = userRepository.findByEmailIgnoreCase(email.trim())
//      .orElseThrow(() -> new RuntimeException("User not found"));
//    return ResponseEntity.ok(fineService.getFinesForUser(user.getId()));
//  }
//
//  @PostMapping("/pay")
//  public ResponseEntity<?> payFine(@Valid @RequestBody FinePaymentRequest req,
//                                   @RequestHeader("Authorization") String authHeader) {
//
//      String email = com.olms.backend.security.SecurityUtil.currentEmail();
//      Long callerId = userRepository.findByEmailIgnoreCase(email.trim())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//
//
//    Fine fine = fineRepository.findById(req.getFineId())
//      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
//
//    boolean isOwner = fine.getUser() != null && callerId.equals(fine.getUser().getId());
//    boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//      .stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ROLE_ADMIN"));
//
//    if (!isOwner && !isAdmin) {
//      return ResponseEntity.status(403).body("Only the fine owner or an admin can pay this fine.");
//    }
//
//    FinePayment payment = fineService.payFine(
//      req.getFineId(),
//      callerId, // paid_by recorded as the caller
//      req.getAmountPaid(),
//      req.getPaymentMethod(),
//      req.getTransactionRef(),
//      req.getNote()
//    );
//    return ResponseEntity.ok(payment);
//  }
//}

// ABhishek 3rd attempt

package com.olms.backend.controller;

import com.olms.backend.dto.FinePaymentRequest;
import com.olms.backend.entities.Fine;
import com.olms.backend.entities.FinePayment;
import com.olms.backend.entities.User;
import com.olms.backend.exception.ResourceNotFoundException;
import com.olms.backend.repository.FineRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.FineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@Validated
public class FineController {

  @Autowired
  private FineService fineService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FineRepository fineRepository;

  // List fines for the currently authenticated user
  @GetMapping("/my")
  public ResponseEntity<List<Fine>> getMyFines() {
    String email = SecurityUtil.currentEmail();
    User user = userRepository.findByEmailIgnoreCase(email.trim())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    List<Fine> fines = fineService.getFinesForUser(user.getId());
    return ResponseEntity.ok(fines);
  }

  // // Pay a fine (owner or ADMIN only)
  // @PostMapping("/pay")
  // public ResponseEntity<?> payFine(@Valid @RequestBody FinePaymentRequest req)
  // {
  // // who is calling?
  // String email = SecurityUtil.currentEmail();
  // User caller = userRepository.findByEmailIgnoreCase(email.trim())
  // .orElseThrow(() -> new ResourceNotFoundException("User not found"));

  // // which fine?
  // Fine fine = fineRepository.findById(req.getFineId())
  // .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));

  // // permission: owner or admin
  // boolean isOwner = fine.getUser() != null
  // && fine.getUser().getId() != null
  // && fine.getUser().getId().equals(caller.getId());

  // // IMPORTANT: your app grants "ADMIN" (not "ROLE_ADMIN")
  // boolean isAdmin = SecurityUtil.isAdmin();

  // if (!isOwner && !isAdmin) {
  // return ResponseEntity.status(403).body("Only the fine owner or an admin can
  // pay this fine.");
  // }

  // // proceed
  // FinePayment payment = fineService.payFine(
  // req.getFineId(),
  // caller.getId(), // paid_by = caller
  // req.getAmountPaid(),
  // req.getPaymentMethod(),
  // req.getTransactionRef(),
  // req.getNote()
  // );
  // return ResponseEntity.ok(payment);
  // }

  // Snigdha
  // Pay a fine (ADMIN only)
  @PostMapping("/pay")
  public ResponseEntity<?> payFine(@Valid @RequestBody FinePaymentRequest req) {
    // ensure only ADMIN can call
    if (!SecurityUtil.isAdmin()) {
      return ResponseEntity.status(403).body("Only ADMIN can pay or resolve fines.");
    }

    // get the logged-in admin
    String email = SecurityUtil.currentEmail();
    User admin = userRepository.findByEmailIgnoreCase(email.trim())
        .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

    // proceed with fine payment (paidBy = admin)
    FinePayment payment = fineService.payFine(
        req.getFineId(),
        admin.getId(),
        req.getAmountPaid(),
        req.getPaymentMethod(),
        req.getTransactionRef(),
        req.getNote());

    return ResponseEntity.ok(payment);
  }
}
