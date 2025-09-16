// com/olms/backend/controller/AdminNotificationController.java
package com.olms.backend.controller;

import com.olms.backend.service.DueNotificationJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

  private final DueNotificationJob job;

  @PostMapping("/run")
  public ResponseEntity<?> runNow() {
    job.runDaily(); // trigger once, synchronously
    return ResponseEntity.ok(Map.of("status","ok","ranAt", Instant.now().toString()));



  }
}
