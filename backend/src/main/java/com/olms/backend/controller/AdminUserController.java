package com.olms.backend.controller;

import com.olms.backend.dto.AddUserRequest;
import com.olms.backend.entities.AccountStatus;
import com.olms.backend.entities.Role;
import com.olms.backend.entities.User;
import com.olms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @PostMapping
  public ResponseEntity<?> addUser(@RequestBody AddUserRequest req) {
    // if (userRepository.existsByEmail(req.getEmail())) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already
    // exists");
    // }
    if (userRepository.existsByEmailIgnoreCase(req.getEmail().trim())) {
      return ResponseEntity.badRequest().body("Email already exists");
    }

    User user = new User();
    user.setName(req.getName());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setPassword(passwordEncoder.encode(req.getPassword()));
    user.setRole("ADMIN".equalsIgnoreCase(req.getRole()) ? Role.ADMIN : Role.USER);
    user.setStatus(AccountStatus.ACTIVE);

    User saved = userRepository.save(user);

    // Return 201 with minimal info
    return ResponseEntity
        .created(URI.create("/api/admin/users/" + saved.getId()))
        .body(Map.of(
            "id", saved.getId(),
            "name", saved.getName(),
            "email", saved.getEmail(),
            "phone", saved.getPhone(),
            "role", saved.getRole().name(),
            "status", saved.getStatus().name()
        ));
  }

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
    var users = userRepository.findAll();
    var result = users.stream().map(u -> Map.<String, Object>of(
        "id", u.getId(),
        "name", u.getName(),
        "email", u.getEmail(),
        "phone", u.getPhone(),
        "role", u.getRole().name(),
        "created_at", u.getCreatedAt())).toList();

    return ResponseEntity.ok(result);
  }
}
