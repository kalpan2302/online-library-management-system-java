package com.olms.backend.controller;

import com.olms.backend.dto.AddUserRequest;
import com.olms.backend.dto.LoginRequest;
import com.olms.backend.dto.LoginResponse;
import com.olms.backend.dto.RegisterRequest;
import com.olms.backend.entities.Role;
import com.olms.backend.entities.User;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.JwtUtil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            return ResponseEntity.badRequest().body("Email already exists");

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone()); // set phone
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER); // maps to USER in DB

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return ResponseEntity.badRequest().body("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // remove "Bearer "
            String email = jwtUtil.getEmail(token); // use getEmail()

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid token");
        }
    }

    // Add this method inside AuthController
    @PostMapping("/admin/add-user")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest req,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // remove "Bearer "
            String callerRole = jwtUtil.getRole(token);

            if (!"ADMIN".equals(callerRole)) {
                return ResponseEntity.status(403).body("Only admins can add users.");
            }

            if (userRepository.existsByEmail(req.getEmail()))
                return ResponseEntity.badRequest().body("Email already exists");

            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPhone(req.getPhone());
            user.setPassword(passwordEncoder.encode(req.getPassword()));

            user.setRole(Role.ADMIN);
            if ("USER".equalsIgnoreCase(req.getRole())) {
                user.setRole(Role.USER);
            }

            userRepository.save(user);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid token or request");
        }
    }

    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // remove "Bearer "
            String callerRole = jwtUtil.getRole(token);

            if (!"ADMIN".equals(callerRole)) {
                return ResponseEntity.status(403).body("Only admins can view all users.");
            }

            // fetch all users
            var users = userRepository.findAll();

            List<Map<String, Object>> result = users.stream()
                    .map(u -> Map.<String, Object>of(
                            "id", u.getId(), // use the correct ID getter from your entity
                            "name", u.getName(),
                            "email", u.getEmail(),
                            "phone", u.getPhone(),
                            "role", u.getRole().name(),
                            "created_at", u.getCreatedAt()))
                    .toList();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid token or request");
        }
    }

}
