//package com.olms.backend.controller;
//
//import com.olms.backend.dto.FineAdjustRequest;
//import com.olms.backend.entities.Fine;
//import com.olms.backend.service.FineService;
//import com.olms.backend.security.JwtUtil;
//import com.olms.backend.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin/fines")
//public class AdminFineController {
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
//  @GetMapping("/all")
//  public ResponseEntity<?> getAllFines(@RequestHeader("Authorization") String authHeader) {
//    // admin-only path (security config ensures ADMIN role)
//    List<Fine> fines = fineService.getAllFines();
//    return ResponseEntity.ok(fines);
//  }
//
//  @PostMapping("/{fineId}/adjust")
//  public ResponseEntity<?> adjustFine(@PathVariable Long fineId, @RequestBody FineAdjustRequest req, @RequestHeader("Authorization") String authHeader) {
//    Fine f = fineService.adjustFine(fineId, req.getNewAmount(), req.getNote());
//    return ResponseEntity.ok(f);
//  }
//
//  @PostMapping("/{fineId}/waive")
//  public ResponseEntity<?> waiveFine(@PathVariable Long fineId, @RequestBody FineAdjustRequest req, @RequestHeader("Authorization") String authHeader) {
//    Fine f = fineService.waiveFine(fineId, req.getNote());
//    return ResponseEntity.ok(f);
//  }
//}



package com.olms.backend.controller;

import com.olms.backend.dto.FineAdjustRequest;
import com.olms.backend.entities.Fine;
import com.olms.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/fines")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminFineController {

  @Autowired
  private FineService fineService;

  @GetMapping("/all")
  public ResponseEntity<List<Fine>> getAllFines() {
    return ResponseEntity.ok(fineService.getAllFines());
  }

  @PostMapping("/{fineId}/adjust")
  public ResponseEntity<Fine> adjustFine(@PathVariable Long fineId,
                                         @Valid @RequestBody FineAdjustRequest req) {
    return ResponseEntity.ok(fineService.adjustFine(fineId, req.getNewAmount(), req.getNote()));
  }

  @PostMapping("/{fineId}/waive")
  public ResponseEntity<Fine> waiveFine(@PathVariable Long fineId,
                                        @Valid @RequestBody FineAdjustRequest req) {
    return ResponseEntity.ok(fineService.waiveFine(fineId, req.getNote()));
  }
}
