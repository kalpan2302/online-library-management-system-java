package com.olms.backend.controller;

import com.olms.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// com/olms/backend/controller/AdminMailTestController.java
@RestController
@RequestMapping("/api/admin/test-mail")
@RequiredArgsConstructor
public class AdminMailTestController {
  private final EmailService emailService;

  @PostMapping
  public ResponseEntity<?> send(@RequestParam String to) {
    emailService.sendPlain(to, "OLMS mail test", "If you see this, SMTP is OK.");
    return ResponseEntity.ok(Map.of("sent", true));
  }
}
