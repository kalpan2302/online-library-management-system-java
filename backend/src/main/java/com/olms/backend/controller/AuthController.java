//package com.olms.backend.controller;
//
//import com.olms.backend.dto.AddUserRequest;
//import com.olms.backend.dto.LoginRequest;
//import com.olms.backend.dto.LoginResponse;
//import com.olms.backend.dto.RegisterRequest;
//import com.olms.backend.entities.Role;
//import com.olms.backend.entities.User;
//import com.olms.backend.repository.UserRepository;
//import com.olms.backend.security.JwtUtil;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//// import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
//        if (userRepository.existsByEmail(req.getEmail()))
//            return ResponseEntity.badRequest().body("Email already exists");
//
//        User user = new User();
//        user.setName(req.getName());
//        user.setEmail(req.getEmail());
//        user.setPhone(req.getPhone()); // set phone
//        user.setPassword(passwordEncoder.encode(req.getPassword()));
//        user.setRole(Role.USER); // maps to USER in DB
//
//        userRepository.save(user);
//        return ResponseEntity.ok("User registered successfully");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
//        User user = userRepository.findByEmail(req.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
//            return ResponseEntity.badRequest().body("Invalid credentials");
//
////        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
////        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
// // Abhishek
//      // inside /auth/login
//      String token = jwtUtil.generateToken(
//        user.getId(),
//        user.getEmail(),
//        user.getRole().name()
//      );
//      return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
//
//    }
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.substring(7); // remove "Bearer "
//            String email = jwtUtil.getEmail(token); // use getEmail()
//
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            return ResponseEntity.ok(Map.of(
//                    "name", user.getName(),
//                    "email", user.getEmail(),
//                    "role", user.getRole().name()));
//        } catch (Exception e) {
//            return ResponseEntity.status(400).body("Invalid token");
//        }
//    }
//
//    // Add this method inside AuthController
//    @PostMapping("/admin/add-user")
//    public ResponseEntity<?> addUser(@RequestBody AddUserRequest req,
//            @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.substring(7); // remove "Bearer "
//            String callerRole = jwtUtil.getRole(token);
//
//            if (!"ADMIN".equals(callerRole)) {
//                return ResponseEntity.status(403).body("Only admins can add users.");
//            }
//
//            if (userRepository.existsByEmail(req.getEmail()))
//                return ResponseEntity.badRequest().body("Email already exists");
//
//            User user = new User();
//            user.setName(req.getName());
//            user.setEmail(req.getEmail());
//            user.setPhone(req.getPhone());
//            user.setPassword(passwordEncoder.encode(req.getPassword()));
//
//            user.setRole(Role.ADMIN);
//            if ("USER".equalsIgnoreCase(req.getRole())) {
//                user.setRole(Role.USER);
//            }
//
//            userRepository.save(user);
//            return ResponseEntity.noContent().build();
//
//        } catch (Exception e) {
//            return ResponseEntity.status(400).body("Invalid token or request");
//        }
//    }
//
//    @GetMapping("/users/all")
//    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = authHeader.substring(7); // remove "Bearer "
//            String callerRole = jwtUtil.getRole(token);
//
//            if (!"ADMIN".equals(callerRole)) {
//                return ResponseEntity.status(403).body("Only admins can view all users.");
//            }
//
//            // fetch all users
//            var users = userRepository.findAll();
//
//            List<Map<String, Object>> result = users.stream()
//                    .map(u -> Map.<String, Object>of(
//                            "id", u.getId(), // use the correct ID getter from your entity
//                            "name", u.getName(),
//                            "email", u.getEmail(),
//                            "phone", u.getPhone(),
//                            "role", u.getRole().name(),
//                            "created_at", u.getCreatedAt()))
//                    .toList();
//
//            return ResponseEntity.ok(result);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(400).body("Invalid token or request");
//        }
//    }
//
//}

//
//// Abhishek
//
//
//package com.olms.backend.controller;
//
//import com.olms.backend.dto.LoginRequest;
//import com.olms.backend.dto.LoginResponse;
//import com.olms.backend.dto.RegisterRequest;
//import com.olms.backend.entities.Role;
//import com.olms.backend.entities.User;
//import com.olms.backend.repository.UserRepository;
//import com.olms.backend.security.JwtUtil;
//
//import java.util.Map;
//
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//  @Autowired private UserRepository userRepository;
//  @Autowired private BCryptPasswordEncoder passwordEncoder;
//  @Autowired private JwtUtil jwtUtil;
//
//  @PostMapping("/register")
//  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
//    if (userRepository.existsByEmail(req.getEmail())) {
//      return ResponseEntity.badRequest().body("Email already exists");
//    }
//
//    User user = new User();
//    user.setName(req.getName());
//    user.setEmail(req.getEmail());
//    user.setPhone(req.getPhone());
//    user.setPassword(passwordEncoder.encode(req.getPassword()));
//    user.setRole(Role.USER);
//
//    userRepository.save(user);
//    return ResponseEntity.ok("User registered successfully");
//  }
//
////  @PostMapping("/login")
////  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
//////    User user = userRepository.findByEmail(req.getEmail())
//////      .orElseThrow(() -> new RuntimeException("User not found"));
////    User user = userRepository
////      .findByEmailIgnoreCase(req.getEmail().trim())
////      .orElseThrow(() -> new RuntimeException("User not found"));
////
////    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
////      return ResponseEntity.badRequest().body("Invalid credentials");
////    }
////
////    // ✅ JwtUtil requires (userId, email, role)
////    String token = jwtUtil.generateToken(
////      user.getId(),
////      user.getEmail(),
////      user.getRole().name()
////    );
////    return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
////  }
//
//@PostMapping("/login")
//public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
//  var email = req.getEmail().trim();
//  var user = userRepository.findByEmailIgnoreCase(email)
//    .orElseThrow(() -> new RuntimeException("User not found"));
//  if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
//    return ResponseEntity.badRequest().body("Invalid credentials");
//
//  String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
//  return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
//}
//
//
////  @GetMapping("/profile")
////  public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
////    try {
////      String token = authHeader.substring(7);
////      String email = jwtUtil.getEmail(token);
////
//////      User user = userRepository.findByEmail(email)
//////        .orElseThrow(() -> new RuntimeException("User not found"));
////      User user = userRepository
////        .findByEmailIgnoreCase(email.trim())
////        .orElseThrow(() -> new RuntimeException("User not found"));
////
////      return ResponseEntity.ok(Map.of(
////        "name", user.getName(),
////        "email", user.getEmail(),
////        "role", user.getRole().name()
////      ));
////    } catch (Exception e) {
////      return ResponseEntity.status(400).body("Invalid token");
////    }
////  }
//
//
//
//  // Abhishek
//
//@GetMapping("/profile")
//public ResponseEntity<?> getProfile() {
//  String email = com.olms.backend.security.SecurityUtil.currentEmail();
//  var user = userRepository.findByEmailIgnoreCase(email.trim())
//    .orElseThrow(() -> new RuntimeException("User not found"));
//  return ResponseEntity.ok(Map.of("name", user.getName(), "email", user.getEmail(), "role", user.getRole().name()));
//}
//}


package com.olms.backend.controller;

import com.olms.backend.dto.LoginRequest;
import com.olms.backend.dto.LoginResponse;
import com.olms.backend.dto.RegisterRequest;
import com.olms.backend.entities.AccountStatus;
import com.olms.backend.entities.Role;
import com.olms.backend.entities.User;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.JwtUtil;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.VerificationService;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;   // use interface; your bean can be BCrypt
  private final VerificationService verificationService;
  private final JwtUtil jwtUtil;

  // ========== REGISTER ==========
  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    // If email exists:
    var existing = userRepository.findByEmailIgnoreCase(req.getEmail().trim());
    if (existing.isPresent()) {
      var u = existing.get();
      if (u.getStatus() == AccountStatus.ACTIVE) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
      }
      // still pending → (re)send if allowed
      verificationService.resendIfAllowed(u);
      throw new ResponseStatusException(HttpStatus.OK, "Verification email resent (if needed)");
    }

    // New user → create as PENDING + send link
    User user = User.builder()
      .name(req.getName())
      .email(req.getEmail().trim())
      .phone(req.getPhone())
      .password(passwordEncoder.encode(req.getPassword()))
      .role(Role.USER)
      .status(com.olms.backend.entities.AccountStatus.PENDING)
      .build();

    userRepository.save(user);
    verificationService.issueTokenAndEmail(user);
    // return ResponseEntity.ok("Registered. Check your email to verify your account.");
    return ResponseEntity.ok(Map.of("message", "Registered. Check your email to verify your account."));
  }


  // ========== VERIFY ==========
  @GetMapping("/verify")
  public ResponseEntity<String> verify(@RequestParam("token") String token) {
    verificationService.verifyByPlainToken(token);
    return ResponseEntity.ok("Account verified. You can log in now.");
  }

  // ========== LOGIN ==========
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
    var email = req.getEmail().trim();
    var user = userRepository.findByEmailIgnoreCase(email)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (user.getStatus() != AccountStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please verify your email before login");
    }
    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    // Your JwtUtil requires (userId, email, role)
    String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
    return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
  }

  // ========== RESEND ==========
  @PostMapping("/resend-verification")
  public ResponseEntity<?> resend(@RequestBody Map<String, String> body) {
    var email = body.getOrDefault("email", "").trim();
    if (email.isEmpty()) return ResponseEntity.badRequest().body("Email is required");

    var user = userRepository.findByEmailIgnoreCase(email)
      // Don’t reveal existence — respond 200 in both paths
      .orElse(null);

    if (user == null) return ResponseEntity.ok("If the account exists, we resent the email");
    if (user.getStatus() == AccountStatus.ACTIVE) return ResponseEntity.ok("Account already verified");

    verificationService.resendIfAllowed(user);
    return ResponseEntity.ok("If the account exists, we resent the email");
  }

  // ========== PROFILE ==========
  @GetMapping("/profile")
  public ResponseEntity<?> getProfile() {
    String email = SecurityUtil.currentEmail();
    if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication");
    var user = userRepository.findByEmailIgnoreCase(email.trim())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return ResponseEntity.ok(Map.of(
      "name", user.getName(),
      "email", user.getEmail(),
      "phone",user.getPhone(),
      "role", user.getRole().name()
    ));
  }
}
