// com/olms/backend/controller/NotificationController.java
package com.olms.backend.controller;

import com.olms.backend.repository.NotificationRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationRepository repo;
  private final UserRepository users;

  @GetMapping("/me")
  public Object myNotifications() {
    String email = SecurityUtil.currentEmail();
    var user = users.findByEmailIgnoreCase(email).orElseThrow();
    return repo.findByUserIdOrderBySentAtDesc(user.getId());
  }

  @PostMapping("/{id}/read")
  public void markRead(@PathVariable Long id) {
    var n = repo.findById(id).orElseThrow();
    n.setReadAt(java.time.Instant.now());
    repo.save(n);
  }
}
