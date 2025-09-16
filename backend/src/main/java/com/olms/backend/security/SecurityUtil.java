////package com.olms.backend.security;
////
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.context.SecurityContextHolder;
////
////public final class SecurityUtil {
////  private SecurityUtil() {}
////  public static String currentEmail() {
////    Authentication a = SecurityContextHolder.getContext().getAuthentication();
////    if (a == null || !a.isAuthenticated() || a.getPrincipal() == null)
////      throw new RuntimeException("Not authenticated");
////    return String.valueOf(a.getPrincipal());
////  }
////  public static boolean isAdmin() {
////    Authentication a = SecurityContextHolder.getContext().getAuthentication();
////    return a != null && a.getAuthorities().stream().anyMatch(au -> "ADMIN".equals(au.getAuthority()));
////  }
////}
////
////
////package com.olms.backend.security;
////
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.GrantedAuthority;
////import org.springframework.security.core.context.SecurityContextHolder;
////
////public final class SecurityUtil {
////  private SecurityUtil(){}
////
////  public static String currentEmail() {
////    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
////    if (auth == null || !auth.isAuthenticated()) {
////      throw new IllegalArgumentException("Missing Authorization/Bearer token");
////    }
////    return String.valueOf(auth.getPrincipal());
////  }
////
////  public static boolean isAdmin() {
////    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
////    return auth != null && auth.getAuthorities().stream()
////      .map(GrantedAuthority::getAuthority)
////      .anyMatch("ADMIN"::equals);
////  }
////}
//
//// com/olms/backend/security/SecurityUtil.java
//package com.olms.backend.security;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public final class SecurityUtil {
//  private SecurityUtil() {}
//
//  public static String currentEmail() {
//    Authentication a = SecurityContextHolder.getContext().getAuthentication();
//    return a != null ? String.valueOf(a.getPrincipal()) : null;
//  }
//
//  public static boolean isAdmin() {
//    Authentication a = SecurityContextHolder.getContext().getAuthentication();
//    if (a == null) return false;
//    return a.getAuthorities().stream().anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
//  }
//}

package com.olms.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public final class SecurityUtil {
  private SecurityUtil() {}

  // ====== Spring Security helpers ======
  public static String currentEmail() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return a != null ? String.valueOf(a.getPrincipal()) : null;
  }

  public static boolean isAdmin() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null) return false;
    return a.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .anyMatch("ROLE_ADMIN"::equals);
  }

  // ====== Verification helpers ======
  public static String sha256(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String newVerificationTokenPlain() {
    // 64+ characters, hard to guess
    return UUID.randomUUID().toString().replace("-", "")
      + UUID.randomUUID().toString().replace("-", "");
  }
}
